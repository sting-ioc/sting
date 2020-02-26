require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'
require 'buildr/jacoco'

desc 'sting: A fast, easy to use, compile-time dependency injection toolkit'
define 'sting' do
  project.group = 'org.realityforge.sting'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('sting-ioc/sting')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'The core module'
  define 'core' do
    pom.include_transitive_dependencies << artifact(:javax_annotation)
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' }

    compile.with :javax_annotation

    test.options[:java_args] = ['-ea']

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
  end

  desc 'The Annotation processor'
  define 'processor' do
    pom.dependency_filter = Proc.new { |_| false }

    compile.with :proton_core,
                 :javax_json,
                 :javapoet,
                 :javax_annotation

    test.with :compile_testing,
              :guava,
              :guava_failureaccess,
              :proton_qa,
              :gwt_user,
              Java.tools_jar,
              :truth,
              :junit,
              :hamcrest_core,
              :mockito,
              :javaee_api,
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
    end unless ENV['TEST'] == 'no' || ENV['PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'] == '0.00'

    test.exclude '*ApiDiffTest' if ENV['PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'] == '0.00'

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

  desc 'Performance Tests'
  define 'performance-tests' do
    test.using :testng
    test.options[:properties] = { 'sting.perf.working_directory' => _('generated/perf/test/java').to_s }
    test.options[:java_args] = %w(-ea)
    test.compile.with :gir,
                      :compile_testing,
                      Java.tools_jar,

                      # Code for the Application to compile against
                      Buildr::GWT.dependencies('2.8.2-v20191108'),
                      :jsinterop_base,
                      :elemental2_core,
                      :elemental2_dom,
                      :elemental2_promise,

                      # Sting deps follow
                      project('core').package(:jar),
                      project('core').compile.dependencies,
                      project('processor').package(:jar),
                      project('processor').compile.dependencies,
                      :braincheck,

                      # Dagger deps follow
                      :javax_inject,
                      :javax_inject_sources,
                      :dagger_core,
                      :dagger_core_sources,
                      :dagger_gwt,
                      :dagger_producers,
                      :dagger_spi,
                      :dagger_compiler,
                      :autocommon,
                      :guava,
                      :guava_failureaccess,
                      :kotlinx_metadata_jvm,
                      :kotlin_stdlib,
                      :kotlin_stdlib_common,
                      :googlejavaformat,
                      :errorprone

    task 'generate_build_time_statistics' do
      project.test.compile.invoke
      cp = project.test.compile.dependencies.map(&:to_s) + [project.test.compile.target.to_s]

      %w(tiny small medium large huge).each do |variant|

        properties = {
          'sting.perf.working_directory' => project._('generated/perf'),
          'sting.perf.fixture_dir' => project._('src/test/fixtures'),
          'sting.perf.variant' => variant,
          'sting.next.version' => ENV['PRODUCT_VERSION'] || p.version,
        }
        Java::Commands.java 'sting.performance.BuildTimePerformanceTest', { :classpath => cp, :properties => properties }
      end
    end

    task 'generate_size_statistics' do
      project.test.compile.invoke
      cp = project.test.compile.dependencies.map(&:to_s) + [project.test.compile.target.to_s]

      %w(eager_tiny tiny lazy_tiny eager_small small lazy_small eager_medium medium lazy_medium eager_large large lazy_large eager_huge huge lazy_huge).each do |variant|
        properties = {
          'sting.perf.working_directory' => project._('generated/perf'),
          'sting.perf.fixture_dir' => project._('src/test/fixtures'),
          'sting.perf.variant' => variant,
          'sting.next.version' => ENV['PRODUCT_VERSION'] || p.version,
        }
        Java::Commands.java 'sting.performance.CodeSizePerformanceTest', { :classpath => cp, :properties => properties }
      end
    end
  end

  desc 'Test Arez in downstream projects'
  define 'downstream-test' do
    compile.with :gir,
                 :javax_annotation

    test.options[:properties] =
      {
        'sting.prev.version' => ENV['PREVIOUS_PRODUCT_VERSION'] || project.version,
        'sting.next.version' => ENV['PRODUCT_VERSION'] || project.version,
        'sting.next.jar' => project('core').package(:jar).to_s,
        'sting.build_j2cl_variants' => (ENV['J2CL'] != 'no'),
        'sting.deploy_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'sting.deploy_test.work_dir' => _(:target, 'deploy_test/workdir').to_s
      }
    test.options[:java_args] = ['-ea']

    local_test_repository_url = URI.join('file:///', project._(:target, :local_test_repository)).to_s
    compile.enhance do
      projects_to_upload = projects(%w(core processor))
      old_release_to = repositories.release_to
      begin
        # First we install them in a local repository so we don't have to access the network during local builds
        repositories.release_to = local_test_repository_url
        projects_to_upload.each do |prj|
          prj.packages.each do |pkg|
            # Uninstall version already present in local maven cache
            pkg.uninstall
            # Install version into local repository
            pkg.upload
          end
        end
        if ENV['STAGE_RELEASE'] == 'true'
          # Then we install it to a remote repository so that TravisCI can access the builds when it attempts
          # to perform a release
          repositories.release_to =
            { :url => 'https://stocksoftware.jfrog.io/stocksoftware/staging', :username => ENV['STAGING_USERNAME'], :password => ENV['STAGING_PASSWORD'] }
          projects_to_upload.each do |prj|
            prj.packages.each(&:upload)
          end
        end
      ensure
        repositories.release_to = old_release_to
      end
    end unless ENV['TEST'] == 'no' # These artifacts only required when running tests.

    test.compile.enhance do
      cp = project.compile.dependencies.map(&:to_s) + [project.compile.target.to_s]

      properties = {}
      # Take the version that we are releasing else fallback to project version
      properties['sting.prev.version'] = ENV['PREVIOUS_PRODUCT_VERSION'] || project.version
      properties['sting.next.version'] = ENV['PRODUCT_VERSION'] || project.version
      properties['sting.build_j2cl_variants'] = ENV['J2CL'] != 'no'
      properties['sting.deploy_test.work_dir'] = _(:target, 'deploy_test/workdir').to_s
      properties['sting.deploy_test.fixture_dir'] = _('src/test/resources/fixtures').to_s
      properties['sting.deploy_test.local_repository_url'] = local_test_repository_url
      properties['sting.deploy_test.store_statistics'] = ENV['STORE_BUILD_STATISTICS'] == 'true'
      properties['sting.deploy_test.build_before'] = (ENV['STORE_BUILD_STATISTICS'] != 'true' && ENV['BUILD_BEFORE'] != 'no')

      Java::Commands.java 'sting.downstream.CollectDrumLoopBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
      Java::Commands.java 'sting.downstream.CollectFluxChallengeBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
      Java::Commands.java 'sting.downstream.CollectBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
    end

    # Only run this test when preparing for release, never on TravisCI (as produces different byte sizes)
    test.exclude '*BuildStatsTest' if ENV['PRODUCT_VERSION'].nil? || ENV['BUILD_STATS'] == 'no' || !ENV['TRAVIS_BUILD_NUMBER'].nil?

    test.using :testng
    test.compile.with :javax_annotation,
                      :javacsv,
                      :gwt_symbolmap,
                      :javax_json,
                      :testng

    project.jacoco.enabled = false
  end

  doc.from(projects(%w(core processor))).
    using(:javadoc,
          :windowtitle => 'Sting API Documentation',
          :linksource => true,
          :link => %w(https://sting-ioc.github.io/api https://docs.oracle.com/javase/8/docs/api),
          :group => {
            'Core' => 'sting.*',
            'Compiler' => 'sting.processor*'
          }
    )
  cleanup_javadocs(project, 'sting')

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
