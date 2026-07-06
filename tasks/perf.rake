require 'shellwords'

BUILD_TIME_VARIANTS = %w(tiny small medium large huge)
CODE_SIZE_VARIANTS = %w(eager_tiny tiny lazy_tiny eager_small small lazy_small eager_medium medium lazy_medium eager_large large lazy_large eager_huge huge lazy_huge)
PERFORMANCE_DATA_DIR = 'performance-benchmarks/data'

def performance_dagger_version
  Buildr.artifact(:dagger_core).version
end

def performance_comparison(version = ENV['PRODUCT_VERSION'])
  "sting-#{version}__dagger-#{performance_dagger_version}"
end

def run_performance_benchmark(args)
  sh(['bazel', 'run', '//performance-benchmarks:benchmark', '--', *args].shelljoin)
end

desc 'Update the code-size statistics stored for next version'
task 'update_code_size_statistics' do
  derive_versions

  if ENV['PERF_SMOKE'] == 'true'
    run_performance_benchmark([
                                '--mode=code-size',
                                '--variant=tiny',
                                '--output-dir=tmp/perf-smoke',
                                "--sting-version=#{ENV['PRODUCT_VERSION']}",
                                "--dagger-version=#{performance_dagger_version}"
                              ])
  else
    CODE_SIZE_VARIANTS.each do |variant|
      run_performance_benchmark([
                                  '--mode=code-size',
                                  "--variant=#{variant}",
                                  "--output-dir=#{PERFORMANCE_DATA_DIR}",
                                  "--sting-version=#{ENV['PRODUCT_VERSION']}",
                                  "--dagger-version=#{performance_dagger_version}"
                                ])
    end
    task('gen_code_size_table').invoke
  end
end

task 'gen_code_size_table' do
  derive_versions

  run_performance_benchmark([
                              '--mode=render-tables',
                              "--data-dir=#{PERFORMANCE_DATA_DIR}",
                              "--code-size-comparison=#{performance_comparison}",
                              '--code-size-output=website/includes/CodeSizeTable.html'
                            ])
end

desc 'Update the build-time statistics stored for next version'
task 'update_build_time_statistics' do
  derive_versions

  if ENV['PERF_SMOKE'] == 'true'
    run_performance_benchmark([
                                '--mode=build-times',
                                '--variant=tiny',
                                '--warmup-seconds=0',
                                '--trials=1',
                                '--output-dir=tmp/perf-smoke',
                                "--sting-version=#{ENV['PRODUCT_VERSION']}",
                                "--dagger-version=#{performance_dagger_version}"
                              ])
  else
    BUILD_TIME_VARIANTS.each do |variant|
      run_performance_benchmark([
                                  '--mode=build-times',
                                  "--variant=#{variant}",
                                  "--output-dir=#{PERFORMANCE_DATA_DIR}",
                                  "--sting-version=#{ENV['PRODUCT_VERSION']}",
                                  "--dagger-version=#{performance_dagger_version}"
                                ])
    end
    task('gen_build_time_table').invoke
  end
end

task 'gen_build_time_table' do
  derive_versions

  run_performance_benchmark([
                              '--mode=render-tables',
                              "--data-dir=#{PERFORMANCE_DATA_DIR}",
                              "--build-times-comparison=#{performance_comparison}",
                              '--build-times-output=website/includes/BuildTimesTable.html'
                            ])
end
