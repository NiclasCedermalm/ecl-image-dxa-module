ECL Image DXA Module
======================

This is a simple module to render ECL images in DXA in a generic way. By default in DXA you need to bind each ECL schema to a semantic class. This module allows you to define your ECL types in your configuration instead. The module works with both local ECL images (published to the site) and referred by an URL/template fragment (pointing to an image in a CDN location).

Prerequisites
----------------

The module has been developed using DXA 1.7 and the DXA white label design. DXA 1.8 can be used as well where the pom.xml/package.config needs to be updated to use 1.8 instead of 1.7. Other designs can be used as well, you might need to adjust the HTML generate method in the ECLImage class to fit your design (handling of responsive images etc).

Setup
-------

Compile the DXA module (there are variants for both .NET and Java) and install it the usual way into your DXA web application (add new JAR/DLL/Area etc).

Configuration
---------------

DXA.NET:

Add the following to the 'appSettings-section in your Web.config:
```
  <add key="ecl-image-types" value="[ECL types separated by comma, e.g. Bynder,MediaBeacon]"/>
  <add key="ecl-image-use-template-fragment" value="[Enable ECL template fragments (true/false)]"/>
  <add key="ecl-image-generate-responsive-images" value="[Use DXA responsive images (true/false)]"/>
```

DXA.Java:

Add the following to the dxa.properties file of your web application:
```
ecl.image.types = [ECL types separated by comma, e.g. Bynder,MediaBeacon]
ecl.image.useTemplateFragment = [Enable ECL template fragments (true/false)]
ecl.image.generateResponsiveImages = [Use DXA responsive images (true/false)]
```

Branching model
----------------

We intend to follow Gitflow (http://nvie.com/posts/a-successful-git-branching-model/) with the following main branches:

 - master - Stable
 - develop - Unstable
 - release/x.y - Release version x.y

Please submit your pull requests on develop. In the near future we intend to push our changes to develop and master from our internal repositories, so you can follow our development process.

License
---------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
