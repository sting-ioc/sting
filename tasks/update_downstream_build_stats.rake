desc 'Update the statistics stored for next version'
task 'update_downstream_build_stats' do
  derive_versions

  sh "buildr clean sting:core:test:compile TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end
