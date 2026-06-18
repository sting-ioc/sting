require 'buildr/api_diff_tool'

desc 'Update the api_differences for the next version'
task 'update_api_diff' do
  Buildr::ReleaseTool.derive_versions_from_changelog
  sh "buildr clean do_update_api_diff TEST=no GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']}"
end

task 'do_update_api_diff' do
  a = Buildr.project('sting:core').package(:jar)
  a.invoke
  Buildr::ApiDiffTool.update_differences_report('org.realityforge.sting:sting-core:jar', ENV['PREVIOUS_PRODUCT_VERSION'], ENV['PRODUCT_VERSION'], a.to_s, "#{WORKSPACE_DIR}/api-test/core", :support_libs => [Buildr.artifact(:javax_annotation).to_s])
  # a = Buildr.project('sting:processor-spi').package(:jar)
  # a.invoke
  # Buildr::ApiDiffTool.update_differences_report('org.realityforge.sting:sting-processor-spi:jar', ENV['PREVIOUS_PRODUCT_VERSION'], ENV['PRODUCT_VERSION'], a.to_s, "#{WORKSPACE_DIR}/api-test/processor-spi", :support_libs => [Buildr.artifact(:javax_annotation).to_s])
  core_diff = "#{WORKSPACE_DIR}/api-test/core/#{ENV['PREVIOUS_PRODUCT_VERSION']}-#{ENV['PRODUCT_VERSION']}.json"
  processor_spi_diff = "#{WORKSPACE_DIR}/api-test/processor-spi/#{ENV['PREVIOUS_PRODUCT_VERSION']}-#{ENV['PRODUCT_VERSION']}.json"
  if File.exist?(core_diff) || File.exist?(processor_spi_diff)
    data = []
    data.concat(JSON.parse(File.read(core_diff))) if File.exist?(core_diff)
    data.concat(JSON.parse(File.read(processor_spi_diff))) if File.exist?(processor_spi_diff)
    diff = "#{WORKSPACE_DIR}/api-test/#{ENV['PREVIOUS_PRODUCT_VERSION']}-#{ENV['PRODUCT_VERSION']}.json"
    File.write(diff, JSON.pretty_generate(data))
  end
end

desc 'Test the api differences for the next version'
task 'test_api_diff' do
  Buildr::ReleaseTool.derive_versions_from_changelog
  sh "buildr clean do_test_api_diff TEST=no GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']}"
end

task 'do_test_api_diff' do
  a = Buildr.project('sting:core').package(:jar)
  a.invoke
  Buildr::ApiDiffTool.test_differences_report('org.realityforge.sting:sting-core:jar', ENV['PREVIOUS_PRODUCT_VERSION'], ENV['PRODUCT_VERSION'], a.to_s, "#{WORKSPACE_DIR}/api-test/core", :support_libs => [Buildr.artifact(:javax_annotation).to_s])
  # a = Buildr.project('sting:processor-spi').package(:jar)
  # a.invoke
  # Buildr::ApiDiffTool.test_differences_report('org.realityforge.sting:sting-processor-spi:jar', ENV['PREVIOUS_PRODUCT_VERSION'], ENV['PRODUCT_VERSION'], a.to_s, "#{WORKSPACE_DIR}/api-test/processor-spi", :support_libs => [Buildr.artifact(:javax_annotation).to_s])
end
