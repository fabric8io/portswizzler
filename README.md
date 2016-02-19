## Port Swizzler

This is a simple tool to make it easy to swizzle ports in an installation of an application so that ports don't clash.

The main use case is if you are provisoining multiple microservices on to the same physical host using [kansible](https://github.com/fabric8io/kansible) as you can't use Docker due to being on Windows / AIX / Solaris / HPUX or an older linux

So once your Ansible playbook as provisioned your application, you can run Port Swizzler on any configuration files or shell scripts which define port values.

Port Swizzler will check if the application has already swizzled the ports; if so it'll reuse the same port. If not it'll replace the ports with unique values for the application name.

