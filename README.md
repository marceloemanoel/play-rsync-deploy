play-rsync-deploy
=================

Deploy a play application using rsync


###Configuration

Create a file `rsyncDeploy.sbt` inside the `project` folder containing the following:

```Scala
resolvers += "bintray.com" at "http://dl.bintray.com/sbt/sbt-plugin-releases"

addSbtPlugin("com.github.marceloemanoel" %% "play-rsync-deploy" % "0.1")
```

In your `build.sbt` file include your project's specific settings, for example:

```Scala
deploy.userName := "username"

deploy.password := Some("password")

deploy.serverAddress := "localhost"
```

And execute the task `rsyncDeploy`
