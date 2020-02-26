BUILD_TIME_VARIANTS = %w(tiny small medium large huge)
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

  dagger_version = Buildr.artifact(:dagger_core).version

  str = <<HTML
<table>
  <caption align="bottom">Code Size Comparison between Sting v#{ENV['PRODUCT_VERSION']} and Dagger v#{dagger_version}</caption>
  <thead>
  <tr>
    <th>Variant</th>
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

  sh "buildr clean sting:performance-tests:generate_build_time_statistics gen_build_time_table DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end

task 'gen_build_time_table' do
  derive_versions

  properties = {}
  IO.read("#{WORKSPACE_DIR}/performance-tests/src/test/fixtures/build-times.properties").split("\n").each do |line|
    values = line.split('=')
    key = values[0]
    value = values[1]
    if key =~ /^#{ENV['PRODUCT_VERSION']}\./
      properties[key[ENV['PRODUCT_VERSION'].length + 1, key.length]] = value
    end
  end

  dagger_version = Buildr.artifact(:dagger_core).version

  str = <<HTML
<table>
  <caption align="bottom">Build Time Comparison between Sting v#{ENV['PRODUCT_VERSION']} and Dagger v#{dagger_version}</caption>
  <thead>
  <tr>
    <th>Variant</th>
    <th>Component Count</th>
    <th>Full Compile</th>
    <th>Incremental Compile</th>
  </tr>
  </thead>
  <tbody>
HTML
=begin
| Scenario | Object Count | Full Compile | Incremental Recompile |
|----------|--------------|--------------|-----------------------|
| Tiny     | 10           | 1.017        | 1.099                 |
| Small    | 50           | 0.648        | 1.716                 |
| Medium   | 250          | 0.764        | 3.605                 |
| Large    | 500          | 0.710        | 4.745                 |
| Huge     | 1000         | 0.704        | 11.785                |

0.05.huge.input.eagerCount=500
0.05.huge.input.inputsPerNode=5
0.05.huge.input.layerCount=10
0.05.huge.input.measureTrials=10
0.05.huge.input.nodesPerLayer=100
0.05.huge.input.warmupTimeInSeconds=20
0.05.huge.output.sting2dagger.all.min=0.705
0.05.huge.output.sting2dagger.incremental.min=9.813

=end
  BUILD_TIME_VARIANTS.each do |variant|
    label = (variant[0, 1].upcase + variant[1, variant.length]).gsub(/_[a-z]/) {|s| " #{s[1].upcase}"}
    layerCount = properties["#{variant}.input.layerCount"].to_i
    nodesPerLayer = properties["#{variant}.input.nodesPerLayer"].to_i
    nodeCount = layerCount * nodesPerLayer
    full = properties["#{variant}.output.sting2dagger.all.min"]
    incremental = properties["#{variant}.output.sting2dagger.incremental.min"]

    str += <<HTML
  <tr>
    <td>#{label}</td>
    <td>#{nodeCount}</td>
    <td>#{full}</td>
    <td>#{incremental}</td>
  </tr>
HTML
  end

  str += <<HTML
  </tbody>
</table>
HTML

  IO.write("#{WORKSPACE_DIR}/website/includes/BuildTimesTable.html", str)
end
