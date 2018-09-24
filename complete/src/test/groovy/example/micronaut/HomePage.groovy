package example.micronaut

import geb.Page

class HomePage extends Page {

    static url = "/"

    static at = { title == 'Micronaut'}

    static content = {
        inputFile { $('input#file', 0) }
        uploadButton { $('#uploadBtn') }
        deleteButton(required: false) { $('#deleteBtn') }
        image(required: false) { $("img#image", 0) }
        uploadForm { $('form', action: '/upload') }
    }

    void uploadFile(String absolutePath) {
        uploadForm.file = absolutePath
        uploadButton.click()
    }

    void delete() {
        if(!deleteButton.empty) {
            deleteButton.click()
        }
    }

    boolean hasImage() {
        !image.empty
    }
}
