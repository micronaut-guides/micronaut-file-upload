package example.micronaut

import geb.Page

class HomePage extends Page {

    static url = "/"

    static at = { title == 'Micronaut'}

    static content = {
        deleteButton(required: false) { $('#deleteBtn') }
        image(required: false) { $("img#image", 0) }

        uploadButton { $('#uploadBtn') }
        uploadForm { $('form', action: '/upload') }

        streamingUploadBtn { $('#streamingUploadBtn') }
        streamingUploadForm { $('form', action: '/uploadstreaming') }
    }

    void uploadFile(String absolutePath) {
        uploadForm.file = absolutePath
        uploadButton.click()
    }

    void uploadStreamingFile(String absolutePath) {
        streamingUploadForm.file = absolutePath
        streamingUploadBtn.click()
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
