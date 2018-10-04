package example.micronaut

import geb.Page

class CreatePage extends Page {

    static url = "/create"

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
