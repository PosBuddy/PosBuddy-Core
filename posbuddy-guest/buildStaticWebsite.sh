#!/bin/bash
clear
echo "build posBuddy Guest App for static Webserver"
echo "copy the build artefacts "
echo "from posbuddy-guest/build_static_website/browser to folder /posBuddy on webserver"

ng build --output-path=build_static_website --base-href=posBuddy
