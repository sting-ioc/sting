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
    compile.with :javax_annotation,
                 :javax_inject

    test.options[:java_args] = ['-ea']

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with :guiceyloops
  end

  desc 'The Annotation processor'
  define 'processor' do
    compile.with :autocommon,
                 :proton_core,
                 :javapoet,
                 :guava,
                 :javax_annotation

    test.with :compile_testing,
              :proton_qa,
              Java.tools_jar,
              :truth,
              :junit,
              :hamcrest_core,
              project('core').package(:jar),
              project('core').compile.dependencies

    package(:jar)
    package(:sources)
    package(:javadoc)

    package(:jar).enhance do |jar|
      jar.merge(artifact(:javapoet))
      jar.merge(artifact(:guava))
      jar.merge(artifact(:proton_core))
      jar.enhance do |f|
        shaded_jar = (f.to_s + '-shaded')
        Buildr.ant 'shade_jar' do |ant|
          artifact = Buildr.artifact(:shade_task)
          artifact.invoke
          ant.taskdef :name => 'shade', :classname => 'org.realityforge.ant.shade.Shade', :classpath => artifact.to_s
          ant.shade :jar => f.to_s, :uberJar => shaded_jar do
            ant.relocation :pattern => 'com.squareup.javapoet', :shadedPattern => 'sting.processor.vendor.javapoet'
            ant.relocation :pattern => 'com.google', :shadedPattern => 'sting.processor.vendor.google'
            ant.relocation :pattern => 'org.realityforge.proton', :shadedPattern => 'sting.processor.vendor.proton'
          end
        end
        FileUtils.mv shaded_jar, f.to_s
      end
    end

    test.using :testng
    test.options[:properties] = { 'sting.fixture_dir' => _('src/test/fixtures') }
    test.compile.with :guiceyloops

    # The generators are configured to generate to here.
    iml.test_source_directories << _('generated/processors/test/java')

    iml.test_source_directories << _('src/test/fixtures/input')
    iml.test_source_directories << _('src/test/fixtures/expected')
    iml.test_source_directories << _('src/test/fixtures/bad_input')
  end

  doc.from(projects(%w(core))).
    using(:javadoc,
          :windowtitle => 'Sting API Documentation',
          :linksource => true,
          :link => %w(https://arez.github.io/arez/api https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/),
          :group => {
            'Core Packages' => 'sting.*',
            'Annotation Packages' => 'sting.annotations*:sting.processor*'
          }
    )

  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dsting.output_fixture_data=false -Dsting.fixture_dir=processor/src/test/fixtures')
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
end
