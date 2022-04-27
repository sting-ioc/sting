require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/top_level_generate_dir'
require 'buildr/gwt'
require 'buildr/shade'

Buildr::MavenCentral.define_publish_tasks(:profile_name => 'org.realityforge', :username => 'realityforge')

desc 'sting: A fast, easy to use, compile-time dependency injection toolkit'
define 'sting' do
  project.group = 'org.realityforge.sting'
  compile.options.source = '17'
  compile.options.target = '17'
  project.compile.options.lint = 'all,-processing,-serial'
  project.compile.options.warnings = true
  project.compile.options.other = %w(-Werror -Xmaxerrs 10000 -Xmaxwarns 10000)

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('sting-ioc/sting')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'The core module'
  define 'core' do
    deps = artifacts(:javax_annotation)
    pom.include_transitive_dependencies << deps
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact]) }

    compile.with deps

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

    test.with :proton_qa,
              :gwt_user,
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
        Buildr::Shade.shade(f,
                            f,
                            'com.squareup.javapoet' => 'sting.processor.vendor.javapoet',
                            'org.realityforge.proton' => 'sting.processor.vendor.proton')
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

  desc 'Integration Tests'
  define 'integration-tests' do
    test.using :testng
    test.options[:java_args] = ['-ea']
    test.compile.options[:processor] = true
    test.compile.with project('core').package(:jar),
                      project('core').compile.dependencies,
                      project('processor').package(:jar),
                      project('processor').compile.dependencies
  end

  desc 'Performance Tests'
  define 'performance-tests' do
    compile.with :gir,

                 # Code for the Application to compile against
                 Buildr::GWT.dependencies(project.gwt_detect_version(Buildr.artifacts(:gwt_user))),
                 :jsinterop_base,
                 :akasha,

                 # Sting deps follow
                 project('core').package(:jar),
                 project('core').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies,
                 :braincheck,
                 :braincheck_jre,

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
      project.compile.invoke
      cp = project.compile.dependencies.map(&:to_s) + [project.compile.target.to_s]

      BUILD_TIME_VARIANTS.each do |variant|

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
      project.compile.invoke
      cp = project.compile.dependencies.map(&:to_s) + [project.compile.target.to_s]

      CODE_SIZE_VARIANTS.each do |variant|
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
                      :jetbrains_annotations,
                      :javax_json,
                      :testng
  end

  desc 'Sting Examples used in documentation'
  define 'doc-examples' do
    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies

    compile.options[:processor] = true
  end

  doc.from(projects(%w(core processor))).
    using(:javadoc,
          :windowtitle => 'Sting API Documentation',
          :linksource => true,
          :link => %w(https://sting-ioc.github.io/api https://docs.oracle.com/javase/8/docs/api),
          :group => {
            'Core' => 'sting*',
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

  ipr.add_java_configuration(project('sting:performance-tests'),
                             'sting.performance.BuildTimePerformanceTest',
                             :name => 'BuildTimePerformanceTest',
                             :dir => 'file://$PROJECT_DIR$/performance-tests',
                             :jvm_args => "-Dsting.perf.working_directory=generated/perf -Dsting.perf.fixture_dir=src/test/fixtures -Dsting.perf.variant=medium -Dsting.next.version=#{ENV['PRODUCT_VERSION']}")

  ipr.add_component_from_artifact(:idea_codestyle)
  ipr.add_code_insight_settings
  ipr.add_nullable_manager
  ipr.add_javac_settings('-Xlint:all,-processing,-serial -Werror')
end
