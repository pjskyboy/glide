package glide.gradle

import directree.DirTree
import org.gradle.testkit.runner.GradleRunner
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

// Help with Spock:
// - http://spockframework.github.io/spock/docs/1.0/spock_primer.html
// - http://spockframework.github.io/spock/docs/1.0/data_driven_testing.html
// - http://spockframework.github.io/spock/docs/1.0/extensions.html

class GlideConfigIntgTests extends Specification {

    public static final File testProjectDir = new File("build", "test-project-config")

    @Shared List<File> pluginClasspath

    def setupSpec() {    // before-class
        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")

        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        this.pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

        DirTree.create(testProjectDir.absolutePath) {
            dir "app", {
                file "index.html", "<h1>hello world</h1>"
            }
            file "glide.groovy", """\
            app {
                name = "sample"
            }
            environments {
                dev {
                    app {
                        name = "sample-dev"
                    }
                }
            }
            """.stripIndent()

            file "build.gradle", """\
                   plugins {
                    id 'com.appspot.glide-gae'
                   }

                   glide {
                        env = "dev"
                   }

                   appengine {
                        daemon = true
                   }
                """.stripIndent()
        }

    }

    def cleanupSpec() {     // after-class

    }


    @IgnoreRest
    def "should honor env in glide block"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath(pluginClasspath)
                .withArguments('glideSync', '--info', '-s')
                .build()

        then:
        new XmlSlurper().parse(new File(testProjectDir, "build/exploded-app/WEB-INF/appengine-web.xml")).application == "sample-dev"
    }


}

