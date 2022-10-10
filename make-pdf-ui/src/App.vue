<template>
  <div id="app" style="min-height: 0px !important" class="row items-start">
    <router-view></router-view>
  </div>
  <div
    id="app"
    class="row justify-center"
    style="width: 100%; display: flex; max-width: 100%"
  >
    <div class="q-gutter-sm row items-start" style="width: 40%">
      <div class="q-pa-md" style="width: 100%">
        <span class="header-text">Document Upload</span>
        <q-form @submit="exportToPDF" @reset="onReset" class="q-gutter-md">
          <div class="q-pa-md" style="width: 100%">
            <q-input
              v-model="text"
              label="no resize arrow"
              type="textarea"
              style="border: 1px solid grey; padding: 5px"
            />
          </div>
          <div style="margin-top: 20px">
            <q-btn label="Export to PDF" type="submit" color="primary" />
          </div>
        </q-form>
      </div>
    </div>
    <div class="q-gutter-sm row items-start" style="width: 40%">
      <div class="q-pa-md">
        <div class="document-upload">
          <span class="header-text">Document Upload</span>
          <q-uploader url="" style="max-width: 300px" @added="fileSelected" />
        </div>
        <div style="margin-top: 20px; text-align: left">
          <q-btn @click="uploadFile()">Upload</q-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import html2pdf from "html2pdf.js";
import axios from "axios";
import { Notify } from "quasar";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
import { ref } from "vue";
import { notifyMessage } from "./mappers/notify";
import b64toBlob from "b64-to-blob";

pdfMake.vfs = pdfFonts.pdfMake.vfs;

export default {
  name: "app",

  setup() {
    const text = ref(null);

    return {
      text,

      exportToPDF() {
        const data = (text.value && text.value) || null;
        let docDefinition = {
          content: data,
        };

        pdfMake.createPdf(docDefinition).getBuffer(function (buffer) {
          var blob = new Blob([buffer]);
          //let fd = null;
          var reader = new FileReader();
          // this function is triggered once a call to readA  sDataURL returns
          reader.onload = function (event) {
            //resetFormData();
            let fd = new FormData();
            //fd.append("fname", "srikanth.pdf");
            //convert base64 to binary/blob
            let blob1 = fetch(event.target.result).then((r) => r.blob());
            fd.append("file", base64ToBlob(event.target.result));
            axios
              .post(
                "http://localhost:8080/api/email/upload",
                fd,
                createHeaders()
              )
              .then(function (response) {
                if (response && response.data && response?.data?.message) {
                  notifyMessage("teal", response.data.message, "thumb_up");
                }
              })
              .catch(function (response) {
                console.log(response);

                Notify.create({
                  color: "negative",
                  message:
                    response?.message ||
                    "Woah! Danger! You are getting good at this!",
                  icon: "report_problem",
                  position: "top-right",
                });
              });
          };
          // trigger the read from the reader...
          reader.readAsDataURL(blob);
        });
      },

      resetFormData() {
        let fd = new FormData();
        return fd;
      },

      async generatePDF() {
        const emailRequest = { text: data };
        let reader = new FileReader();
        const headers = {
          "Content-Type": "multipart/form-data",
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
          "Access-Control-Allow-Credentials": "true",
          crossDomain: true,
          //"Access-Control-Allow-Methods": "GET, POST, OPTIONS",
          //"Access-Control-Allow-Headers": "Origin, Content-Type, Accept",
        };
        // URL to BLOB
        //let blob1 = await fetch(url).then((r) => r.blob());
        axios
          .post(
            "http://localhost:8080/api/email/makePdfFileSaveToEmail",
            emailRequest,
            headers
          )
          .then(function (response) {
            if (response && response.data && response?.data?.message) {
              notifyMessage("teal", response.data.message, "thumb_up");
            }
          })
          .catch(function (response) {
            console.log(response);
            notifyMessage("negative", response?.message, "report_problem");
          });
      },
    };
  },

  methods: {
    fileSelected(file) {
      this.selected_file = file[0];
      this.check_if_document_upload = true;
    },

    exportToPDF1() {
      html2pdf(this.$refs.document, {
        margin: 1,
        filename: "document.pdf",
        image: { type: "jpeg", quality: 0.98 },
        html2canvas: { dpi: 300, letterRendering: false },
        jsPDF: { unit: "in", format: "letter", orientation: "landscape" },
      });
    },

    uploadFile() {
      let fd = new FormData();
      fd.append("fileName", "Srikanth.pdf");
      fd.append("file", this.selected_file);
      axios
        .post("http://localhost:8080/api/email/upload", fd, createHeaders())
        .then(function (response) {
          console.log(response);
        })
        .catch(function (response) {
          console.log(response);

          Notify.create({
            color: "negative",
            message:
              response?.message ||
              "Woah! Danger! You are getting good at this!",
            icon: "report_problem",
            position: "top-right",
          });
        });
    },

    data() {
      return {
        selected_file: "",
        check_if_document_upload: false,
      };
    },
  },
};

export const createHeaders = () => {
  return {
    "Content-Type": "multipart/form-data",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
    "Access-Control-Allow-Credentials": "true",
    crossDomain: true,
    //"Access-Control-Allow-Methods": "GET, POST, OPTIONS",
    //"Access-Control-Allow-Headers": "Origin, Content-Type, Accept",
  };
};

export const base64ToBlob = (
  base64,
  contentType = "image/png",
  chunkLength = 512
) => {
  const byteCharsArray = Array.from(
    window.atob(base64.substr(base64.indexOf(",") + 1))
  );
  const chunksIterator = new Array(
    Math.ceil(byteCharsArray.length / chunkLength)
  );
  const bytesArrays = [];

  for (let c = 0; c < chunksIterator.length; c++) {
    bytesArrays.push(
      new Uint8Array(
        byteCharsArray
          .slice(c * chunkLength, chunkLength * (c + 1))
          .map((s) => s.charCodeAt(0))
      )
    );
  }

  const blob = new Blob(bytesArrays, {
    filename: "Srikanth.pdf",
    type: contentType,
  });

  return blob;
};
</script>

<style scoped>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.document-upload {
  text-align: left;
}
.header-text {
  font-weight: 900;
}
</style>
