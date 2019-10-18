require 'json'
package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name          = package['name']
  s.version       = package["version"]
  s.summary       = package['description']
  s.author        = { "Hieu Tran" => "hanhieu.a3.namly.2012@gmail.com" }
  s.license       = package['license']
  s.homepage      = package['homepage']
  s.source        = { :git => 'https://github.com/TranHanHieu/react-native-album-list.git' }
  s.platform      = :ios, '9.0'

  s.source_files  = 'ios/**/*.{h,m}'

  s.dependency 'React'

end