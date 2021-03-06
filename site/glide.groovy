app {
    name = "glide-gae"
    version = "beta"

    resourceFiles {
        includes = ["**.gtpl", "**.html", "**.groovy", "**.md", "favicon.ico", "robots.txt", "sitemap.xml"]
    }
}

layout {
    mappings = [
            "/*": "/master_layout.gtpl",
            "/markdown.groovy": ["/docs/layout.gtpl", "/master_layout.gtpl"]
    ]
    excludes = ["/install", "/install.html"]
}

web {
    security = [
            'admin': ["/executor/*"]
    ]

    filters {
        protectedResourcesFilter {
            initParams = [strict: true]
        }
        routesFilter {
            urlPatterns = ['/*']
            dispatchers = [ 'REQUEST', 'ERROR']
        }
    }

}
