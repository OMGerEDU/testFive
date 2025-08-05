# Builds a Windows installer using the jpackage Maven plugin.
# Requires JDK 17 or later on a Windows host.

mvn -q clean package -Pwindows-installer

