require File.expand_path(File.dirname(__FILE__) + '/util')

SITE_DIR = "#{WORKSPACE_DIR}/reports/site"

desc 'Copy the javadocs to docs dir'
task 'site:javadocs' do
  javadocs_dir = "#{WORKSPACE_DIR}/target/sting/doc"
  file(javadocs_dir).invoke
  mkdir_p SITE_DIR
  cp_r javadocs_dir, "#{SITE_DIR}/api"

  # Copy api docs to a location suitable for local development
  cp_r javadocs_dir, "#{WORKSPACE_DIR}/website/static/api"
end

desc 'Copy the favicons to docs dir'
task 'site:favicons' do
  favicons_dir = "#{WORKSPACE_DIR}/assets/favicons"
  mkdir_p SITE_DIR
  cp_r Dir["#{favicons_dir}/*.png"], SITE_DIR
  cp_r Dir["#{favicons_dir}/*.json"], SITE_DIR
  cp_r Dir["#{favicons_dir}/*.xml"], SITE_DIR
  cp_r Dir["#{favicons_dir}/*.ico"], SITE_DIR
end

desc 'Copy the api diff reports to website'
task 'site:apidiff' do
  mkdir_p SITE_DIR
  cp_r "#{WORKSPACE_DIR}/assets/api-diff", "#{SITE_DIR}/api-diff"
  source_dir = "#{WORKSPACE_DIR}/api-test/src/test/resources/fixtures"
  target_dir = "#{SITE_DIR}/api-diff/data/sting"
  mkdir_p target_dir
  if File.exist?(source_dir)
    cp_r Dir["#{source_dir}/*.json"], target_dir
  end
end

desc 'Build the website'
task 'site:build' do
  rm_rf SITE_DIR
  # Remove the static site used when doing local development of website
  rm_rf "#{WORKSPACE_DIR}/website/static/api"
  sh "yarn build #{SITE_DIR}"
  mkdir_p File.dirname(SITE_DIR)
  mv "#{WORKSPACE_DIR}/website/build/sting", SITE_DIR
  task('site:javadocs').invoke
  task('site:apidiff').invoke
  task('site:favicons').invoke
end

desc 'Check that the website does not have any broken links'
task 'site:link_check' do
  require 'webrick'
  require 'socket'

  # Copy the root and replace any absolute paths to target url with relative paths
  # This is required as docusaurus forces qualified paths for some elements (i.e. atom/rss feeds)
  root = "#{WORKSPACE_DIR}/target/site-link-check"
  rm_rf root
  mkdir_p File.dirname(root)
  cp_r SITE_DIR, root

  Dir["#{root}/**/*.html"].each do |filename|
    content = IO.read(filename)
    content = content.gsub('https://sting-ioc.github.io', '')
    IO.write(filename, content)
  end

  # Get a free port and web address
  socket = Socket.new(:INET, :STREAM, 0)
  socket.bind(Addrinfo.tcp('127.0.0.1', 0))
  address = socket.local_address.ip_address
  port = socket.local_address.ip_port
  socket.close

  webserver = WEBrick::HTTPServer.new(:Port => port, :DocumentRoot => root, :AccessLog => [])
  Thread.new {webserver.start}

  trap('INT') {webserver.shutdown}
  begin
    base_url = "http://#{address}:#{port}"
    excludes = []
    excludes << 'https://github.com/sting-ioc/sting/compare/'
    excludes << 'https://github.com/sting-ioc/sting-ioc.github.io/settings'
    excludes << 'https://docs.oracle.com/javase/8/docs/api'
    excludes << 'https://buildr.apache.org'
    excludes << 'https://maven.apache.org'
    excludes << 'https://www.youtube.com'
    excludes << 'https://dagger.dev'
    excludes << 'https://jcp.org'
    # This next line is required if updating docs in branch and adding new
    # pages then this url may not exist until it is merged to master
    excludes << 'https://github.com/sting-ioc/sting/tree/master/docs'
    sh "yarn blc --user-agent SiteChecker --ordered --recursive --filter-level 3 #{base_url} #{excludes.collect {|e| "--exclude #{e}"}.join(' ')}"
  ensure
    webserver.shutdown
  end
end

desc 'Serve the website for developing documentation'
task 'site:serve' do
  sh 'yarn start'
end

desc 'Build the website'
task 'site:deploy' => ['site:build'] do
  # Verify the site is valid first
  task('site:link_check').invoke

  # Only publish the site off the master branch if running out of Travis
  if ENV['TRAVIS_BRANCH'].nil? || ENV['TRAVIS_BRANCH'] == 'master'
    origin_url = 'https://github.com/sting-ioc/sting-ioc.github.io.git'

    travis_build_number = ENV['TRAVIS_BUILD_NUMBER']
    if travis_build_number
      origin_url = origin_url.gsub('https://github.com/', 'git@github.com:')
    end

    local_dir = "#{WORKSPACE_DIR}/target/remote_site"
    rm_rf local_dir

    sh "git clone -b master --depth 1 #{origin_url} #{local_dir}"

    in_dir(local_dir) do
      message = "Publish website#{travis_build_number.nil? ? '' : " - Travis build: #{travis_build_number}"}"

      cp_r Dir["#{SITE_DIR}/*"], local_dir
      sh 'git add . -f'
      unless `git status -s`.strip.empty?
        sh "git commit -m \"#{message}\""
        sh 'git push -f origin master'
      end
    end
  end
end
