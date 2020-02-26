CODE_SIZE_VARIANTS = %w(eager_tiny tiny lazy_tiny eager_small small lazy_small eager_medium medium lazy_medium eager_large large lazy_large eager_huge huge lazy_huge)

desc 'Update the code-size statistics stored for next version'
task 'update_code_size_statistics' do
  derive_versions

  sh "buildr clean sting:performance-tests:generate_size_statistics gen_code_size_table DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end

task 'gen_code_size_table' do
  derive_versions

  properties = {}
  IO.read("#{WORKSPACE_DIR}/performance-tests/src/test/fixtures/code-size.properties").split("\n").each do |line|
    values = line.split('=')
    key = values[0]
    value = values[1]
    if key =~ /^#{ENV['PRODUCT_VERSION']}\./
      properties[key[ENV['PRODUCT_VERSION'].length + 1, key.length]] = value
    end
  end

  str = <<HTML
<table>
  <thead>
  <tr>
    <th>Scenario</th>
    <th>Component Count</th>
    <th>Eager %</th>
    <th>Sting Size</th>
    <th>Dagger Size</th>
    <th>Size Delta</th>
  </tr>
  </thead>
  <tbody>
HTML
  CODE_SIZE_VARIANTS.each do |variant|
    label = (variant[0, 1].upcase + variant[1, variant.length]).gsub(/_[a-z]/) {|s| " #{s[1].upcase}"}
    eagerCount = properties["#{variant}.input.eagerCount"].to_i
    inputsPerNode = properties["#{variant}.input.inputsPerNode"].to_i
    layerCount = properties["#{variant}.input.layerCount"].to_i
    nodesPerLayer = properties["#{variant}.input.nodesPerLayer"].to_i
    nodeCount = layerCount * nodesPerLayer
    eagerPercentage = (eagerCount * 100 / nodeCount).to_i
    dagger = properties["#{variant}.output.dagger.size"].to_i
    sting = properties["#{variant}.output.sting.size"].to_i

    str += <<HTML
  <tr>
    <td>#{label}</td>
    <td>#{nodeCount}</td>
    <td>#{eagerPercentage}%</td>
    <td>#{sting}</td>
    <td>#{dagger}</td>
    <td>+#{dagger - sting}</td>
  </tr>
HTML
  end

  str += <<HTML
  </tbody>
</table>
HTML

  IO.write("#{WORKSPACE_DIR}/website/includes/CodeSizeTable.html", str)
end

desc 'Update the build-time statistics stored for next version'
task 'update_build_time_statistics' do
  derive_versions

  sh "buildr clean sting:performance-tests:generate_build_time_statistics DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end
