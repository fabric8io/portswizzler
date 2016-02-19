## Port Swizzler

This is a simple tool to make it easy to swizzle ports in an installation of an application so that ports don't clash.

The main use case is if you are provisoining multiple microservices on to the same physical host using [kansible](https://github.com/fabric8io/kansible) as you can't use Docker due to being on Windows / AIX / Solaris / HPUX or an older linux

So once your Ansible playbook as provisioned your application, you can run Port Swizzler on any configuration files or shell scripts which define port values.

Port Swizzler will check if the application has already swizzled the ports; if so it'll reuse the same port. If not it'll replace the ports with unique values for the application name.


### Example

First build Port Swizzler and generate some dummy data in `target/cheese`

    mvn clean install
    cp -r src/test/resources target/cheese

Now you should be able to run portswizzler on the command line like this:

    java -jar target/portswizzler-1.0-SNAPSHOT.jar myAppName target/cheese '**/*.cfg' '\.port\s*=\s*(\d+)' target/ports.yml

You should see the ports in the target/cheese/etc/*.cfg files have been changed!

The mappings of the ports for your app should be written to `target/ports.yml` so that it can be made available to [kansible](https://github.com/fabric8io/kansible)
    
The allocated ports are then written to `portswizzler.yml` in the current working directory.    

If you wish to explicitly set the location of the `portswizzler.yml` file then use the `$PORTSWIZZLER_MAPPINGS` environment variable.
