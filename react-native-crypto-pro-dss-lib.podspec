require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-crypto-pro-dss-lib"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-crypto-pro-dss-lib
                   DESC
  s.homepage     = "https://github.com/digitalFrontend/react-native-crypto-pro-dss-lib"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "digitalFrontend" => "digitalFrontend@tele2.ru" }
  s.platforms    = { :ios => "12.0" }
  s.source       = { :git => "https://github.com/digitalFrontend/react-native-crypto-pro-dss-lib.git", :tag => "#{s.version}" }

   s.pod_target_xcconfig = {
    'FRAMEWORK_SEARCH_PATHS' => '"${PODS_ROOT}/../Frameworks"'
  }
  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.ios.vendored_frameworks = [
    'Frameworks/SDKFramework.framework'
  ]
  # ...
  # s.dependency "..."
  
  



end


