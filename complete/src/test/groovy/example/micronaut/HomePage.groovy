package example.micronaut

import geb.Page

class HomePage extends Page {

    static url = "/"

    static at = { title == 'Micronaut'}

    static content = {
        inputFile { $('input#file', 0) }
        inputKey { $('input#key', 0) }
        uploadButton { $('#uploadBtn') }
        deleteButton(required: false) { $('#deleteBtn') }
        image(required: false) { $("img#image", 0) }
        uploadForm { $('form', action: '/upload') }

        streamingInputFile { $('input#streamingfile', 0) }
        streamingInputKey { $('input#streamingkey', 0) }
        streamingUploadBtn { $('#streamingUploadBtn') }
        streamingUploadForm { $('form', action: '/uploadstreaming') }
    }

    void uploadFile(String key, String absolutePath) {
        inputKey = key
        uploadForm.file = absolutePath
        uploadButton.click()
    }

    void uploadStreamingFile(String key, String absolutePath) {
        streamingInputKey = key
        streamingInputFile = absolutePath
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
