package example.micronaut

import geb.Page

class HomePage extends Page {

    static url = "/"

    static at = { title == 'Micronaut'}

    static content = {
        bodyEl { $('body').module(ImageModule) }
    }

    void uploadFile(String absolutePath) {
        bodyEl.uploadFile(absolutePath)
    }

    void delete() {
        bodyEl.delete()
    }

    boolean hasImage() {
        bodyEl.hasImage()
    }
}
