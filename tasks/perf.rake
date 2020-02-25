desc 'Update the code-size statistics stored for next version'
task 'update_code_size_statistics' do
  derive_versions

  sh "buildr clean sting:performance-tests:generate_size_statistics DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end

desc 'Update the build-time statistics stored for next version'
task 'update_build_time_statistics' do
  derive_versions

  sh "buildr clean sting:performance-tests:generate_build_time_statistics DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end
