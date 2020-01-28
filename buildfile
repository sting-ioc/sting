require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'
require 'buildr/jacoco'

desc 'sting: A simple, compile-time dependency injection toolkit'
define 'sting' do
  project.group = 'org.realityforge.sting'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/sting')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'The core module'
  define 'core' do
    compile.with :javax_annotation

    test.options[:java_args] = ['-ea']

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
  end

  desc 'The Annotation processor'
  define 'processor' do
    compile.with :proton_core,
                 :javax_json,
                 :javapoet,
                 :javax_annotation

    test.with :compile_testing,
              :guava,
              :guava_failureaccess,
              :proton_qa,
              Java.tools_jar,
              :truth,
              :junit,
              :hamcrest_core,
              :mockito,
              project('core').package(:jar),
              project('core').compile.dependencies

    package(:jar)
    package(:sources)
    package(:javadoc)

    package(:jar).enhance do |jar|
      jar.merge(artifact(:javapoet))
      jar.merge(artifact(:proton_core))
      jar.enhance do |f|
        shaded_jar = (f.to_s + '-shaded')
        Buildr.ant 'shade_jar' do |ant|
          artifact = Buildr.artifact(:shade_task)
          artifact.invoke
          ant.taskdef :name => 'shade', :classname => 'org.realityforge.ant.shade.Shade', :classpath => artifact.to_s
          ant.shade :jar => f.to_s, :uberJar => shaded_jar do
            ant.relocation :pattern => 'com.squareup.javapoet', :shadedPattern => 'sting.processor.vendor.javapoet'
            ant.relocation :pattern => 'org.realityforge.proton', :shadedPattern => 'sting.processor.vendor.proton'
          end
        end
        FileUtils.mv shaded_jar, f.to_s
      end
    end

    test.using :testng
    test.options[:properties] = { 'sting.fixture_dir' => _('src/test/fixtures') }

    # The generators are configured to generate to here.
    iml.test_source_directories << _('generated/processors/test/java')

    iml.test_source_directories << _('src/test/fixtures/input')
    iml.test_source_directories << _('src/test/fixtures/expected')
    iml.test_source_directories << _('src/test/fixtures/bad_input')
  end

  desc 'Track API Changes'
  define 'api-test' do
    test.compile.with :javax_annotation,
                      :javax_json,
                      :gir

    test.options[:properties] =
      {
        'sting.api_test.store_api_diff' => ENV['STORE_API_DIFF'] == 'true',
        'sting.prev.version' => ENV['PREVIOUS_PRODUCT_VERSION'],
        'sting.prev.jar' => artifact("org.realityforge.sting:sting-core:jar:#{ENV['PREVIOUS_PRODUCT_VERSION'] || project.version}").to_s,
        'sting.next.version' => ENV['PRODUCT_VERSION'],
        'sting.next.jar' => project('core').package(:jar).to_s,
        'sting.api_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'sting.revapi.jar' => artifact(:revapi_diff).to_s
      }
    test.options[:java_args] = ['-ea']
    test.using :testng

    test.compile.enhance do
      mkdir_p _('src/test/resources/fixtures')
      artifact("org.realityforge.sting:sting-core:jar:#{ENV['PREVIOUS_PRODUCT_VERSION']}").invoke
      project('core').package(:jar).invoke
      artifact(:revapi_diff).invoke
    end unless ENV['TEST'] == 'no' || ENV['PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'].nil?

    test.exclude '*ApiDiffTest' if ENV['PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'].nil?

    project.jacoco.enabled = false
  end

  desc 'Integration Tests'
  define 'integration-tests' do
    test.using :testng
    test.options[:java_args] = ['-ea']
    test.compile.with project('core').package(:jar),
                      project('core').compile.dependencies,
                      project('processor').package(:jar),
                      project('processor').compile.dependencies

    # The generators are configured to generate to here.
    iml.test_source_directories << _('generated/processors/test/java')
  end

  doc.from(projects(%w(core))).
    using(:javadoc,
          :windowtitle => 'Sting API Documentation',
          :linksource => true,
          :link => %w(https://sting.github.io/sting/api https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/),
          :group => {
            'Core Packages' => 'sting.*',
            'Annotation Packages' => 'sting.annotations*:sting.processor*'
          }
    )

  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dsting.output_fixture_data=false -Dsting.fixture_dir=processor/src/test/fixtures')

  ipr.add_testng_configuration('processor',
                               :module => 'processor',
                               :jvm_args => '-ea -Dsting.output_fixture_data=true -Dsting.fixture_dir=src/test/fixtures')
  ipr.add_testng_configuration('integration-tests',
                               :module => 'integration-tests',
                               :jvm_args => '-ea -Dsting.output_fixture_data=true')

  ipr.add_component_from_artifact(:idea_codestyle)

  ipr.add_component('CompilerConfiguration') do |component|
    component.annotationProcessing do |xml|
      xml.profile(:default => true, :name => 'Default', :enabled => true) do
        xml.sourceOutputDir :name => 'generated/processors/main/java'
        xml.sourceTestOutputDir :name => 'generated/processors/test/java'
        xml.outputRelativeToContentRoot :value => true
      end
    end
  end

  ipr.add_component('JavacSettings') do |xml|
    xml.option(:name => 'ADDITIONAL_OPTIONS_STRING', :value => '-Xlint:all,-processing,-serial')
  end

  ipr.add_component('JavaProjectCodeInsightSettings') do |xml|
    xml.tag!('excluded-names') do
      xml << '<name>com.sun.istack.internal.NotNull</name>'
      xml << '<name>com.sun.istack.internal.Nullable</name>'
      xml << '<name>org.jetbrains.annotations.Nullable</name>'
      xml << '<name>org.jetbrains.annotations.NotNull</name>'
      xml << '<name>org.testng.AssertJUnit</name>'
    end
  end
  ipr.add_component('NullableNotNullManager') do |component|
    component.option :name => 'myDefaultNullable', :value => 'javax.annotation.Nullable'
    component.option :name => 'myDefaultNotNull', :value => 'javax.annotation.Nonnull'
    component.option :name => 'myNullables' do |option|
      option.value do |value|
        value.list :size => '2' do |list|
          list.item :index => '0', :class => 'java.lang.String', :itemvalue => 'org.jetbrains.annotations.Nullable'
          list.item :index => '1', :class => 'java.lang.String', :itemvalue => 'javax.annotation.Nullable'
        end
      end
    end
    component.option :name => 'myNotNulls' do |option|
      option.value do |value|
        value.list :size => '2' do |list|
          list.item :index => '0', :class => 'java.lang.String', :itemvalue => 'org.jetbrains.annotations.NotNull'
          list.item :index => '1', :class => 'java.lang.String', :itemvalue => 'javax.annotation.Nonnull'
        end
      end
    end
  end
end
