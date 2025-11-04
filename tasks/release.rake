require 'buildr/release_tool'

Buildr::ReleaseTool.define_release_task do |t|
  t.extract_version_from_changelog
  t.zapwhite
  t.ensure_git_clean
  t.verify_no_todo
  t.stage('PerformanceDataPresent', 'Verify that performance data is present for current version') do
    unless ENV['SKIP_PERF_DATA'] == 'true'
      derive_versions

      %w(build-times code-size).each do |type|
        filename = "#{WORKSPACE_DIR}/performance-tests/src/test/fixtures/#{type}.properties"
        unless IO.read(filename).split("\n").any? { |line| line.split('=')[0] =~ /^#{ENV['PRODUCT_VERSION']}\./ }
          raise "No performance data for version #{ENV['PRODUCT_VERSION']} present for performance type #{type}. Add performance data or suppress this check by passing SKIP_PERF_DATA=true"
        end
      end
    end
  end
  t.build(:additional_tasks => "do_test_api_diff J2CL=#{ENV['J2CL']} STAGE_RELEASE=true")
  t.stage('ArchiveDownstream', 'Archive downstream projects that may need changes pushed') do
    unless ENV['BUILD_STATS'] == 'no'
      FileUtils.rm_rf 'archive'
      FileUtils.mkdir_p 'archive'
      mv 'target/sting_downstream-test/deploy_test/workdir', 'archive/downstream'
    end
  end
  t.patch_changelog('sting-ioc/sting',
                    :api_diff_directory => "#{WORKSPACE_DIR}/api-test",
                    :api_diff_website => 'https://sting-ioc.github.io/api-diff?key=sting&')
  t.stage('PatchWebsite', 'Update the version on the website') do
    setup_filename = 'docs/project_setup.md'
    IO.write(setup_filename, IO.read(setup_filename).
      gsub("<version>#{ENV['PREVIOUS_PRODUCT_VERSION']}</version>", "<version>#{ENV['PRODUCT_VERSION']}</version>"))
    sh 'git reset 2>&1 1> /dev/null'
    sh "git add #{setup_filename}"
    # Zapwhite only runs against files added to git so we have to do this dance after adding files
    `bundle exec zapwhite`
    sh 'git reset 2>&1 1> /dev/null'
    sh "git add #{setup_filename}"
    sh "git commit -m \"Update documentation to reflect the #{ENV['PRODUCT_VERSION']} release\""
  end

  t.stage('BuildWebsite', 'Build the website to ensure site still builds') do
    task('site:build').invoke
    task('site:link_check').invoke
  end
  t.tag_project
  t.maven_central_publish
  t.patch_changelog_post_release
  t.stage('PatchStatisticsPostRelease', 'Copy the statistics forward to prepare for next development iteration') do
    filename = 'downstream-test/src/test/resources/fixtures/statistics.properties'
    current_version = ENV['PRODUCT_VERSION']
    next_version = Buildr::ReleaseTool.derive_next_version(ENV['PRODUCT_VERSION'])
    pattern = /^#{current_version}\./

    lines = IO.read(filename).split("\n")
    lines +=
      lines
        .select { |line| line =~ pattern }
        .collect { |line| line.gsub("#{current_version}.", "#{next_version}.") }

    IO.write(filename, lines.sort.uniq.join("\n") + "\n")

    sh "git add #{filename}"
    sh 'git commit -m "Update statistics in preparation for next development iteration"'
  end
  t.push_changes
  t.github_release('sting-ioc/sting')
  t.stage('PushDownstreamChanges', 'Push downstream changes') do
    unless ENV['BUILD_STATS'] == 'no'
      # Push the changes that have been made locally in downstream projects.
      # Artifacts have been pushed to staging repository by this time so they should build
      # even if it has not made it through the Maven release process
      DOWNSTREAM_EXAMPLES.each_pair do |downstream_example, branches|
        sh "cd archive/downstream/#{downstream_example} && git push --all"
        branches.each do |branch|
          full_branch = "#{branch}-StingUpgrade-#{ENV['PRODUCT_VERSION']}"
          `cd archive/downstream/#{downstream_example} && git push origin :#{full_branch} 2>&1`
          puts "Completed remote branch #{downstream_example}/#{full_branch}. Removed." if 0 == $?.exitstatus
        end
      end

      FileUtils.rm_rf 'archive'
    end
  end
end
