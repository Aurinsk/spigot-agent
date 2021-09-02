# spigot-agent

spigot-agent is the monitoring agent for Aurinsk's Minecraft monitoring software.

Usage
-----
Download the latest plugin release from the releases section and upload it to your server's  `plugins` folder.

If you're updating to the latest release, simply delete the old jar file from your `plugins` folder and upload the newly downloaded one.

If you would like to inspect the code yourself and compile, you can view the instructions below.

Compiling
-----
Compiling spigot-agent directly from downloaded code is relatively simple. We use Apache Maven for compiling to allow for easier portability.

The instructions are for a Linux based system as it is what spigot-agent was developed on.

Make sure you've installed Apache Maven and ran BuildTools for 1.17.1 to add the repository to Maven. The install will fail if you have not compiled the server jar on your system.

Information for installing Maven and running BuildTools can be found below:
- [Maven](https://maven.apache.org/install.html)
- [BuildTools](https://www.spigotmc.org/wiki/buildtools/#1-17-1)

You can download the source code from the latest release and extract it, or `git pull` the latest release into a directory of your choice.

```
git pull https://github.com/Aurinsk/spigot-agent.git
```

Once you have done all of the above, you can run one command to compile spigot-agent. Make sure you're located within the directory which you downloaded the files into.

```
mvn clean install
```

The compiled and ready to use jar will be in the `target` folder.
