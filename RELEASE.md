# Releasing to Sonatype

To perform a SNAPSHOT release, do:

```
	gradle -Pprofile=release uploadArchives

```

To perform an actual release do:

```

        gradle -Pprofile=release release

```

Log into https://oss.sonatype.org/index.html#welcome and do the **Close** and **Release**



