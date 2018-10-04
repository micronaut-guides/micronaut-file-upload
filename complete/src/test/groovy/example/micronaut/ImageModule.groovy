package example.micronaut

import geb.Module

class ImageModule extends Module {
    static content = {
        deleteButton(required: false) { $('#deleteBtn') }
        image(required: false) { $("img#image", 0) }
        uploadButton { $('#uploadBtn') }
        uploadForm { $('form', action: contains('/upload')) }
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
