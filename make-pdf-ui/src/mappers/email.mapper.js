import axios from "axios";
import { Notify } from "quasar";
import { notifyMessage } from "./notify";

export const makePDFSendEmailBinary = (docDefinition, loader) => {
  return pdfMake.createPdf(docDefinition).getBuffer(function (buffer) {
    var blob = new Blob([buffer]);
    var reader = new FileReader();
    // this function is triggered once a call to readA  sDataURL returns
    reader.onload = function (event) {
      let fd = new FormData();
      //fd.append("fname", "srikanth.pdf");
      //convert base64 to binary/blob
      loader.show();
      let blob1 = fetch(event.target.result).then((r) => r.blob());
      fd.append("file", base64ToBlob(event.target.result));
      axios
        .post("http://localhost:8080/api/email/upload", fd, createHeaders())
        .then(function (response) {
          loader.hide();
          if (response && response.data && response?.data?.message) {
            notifyMessage("teal", response.data.message, "thumb_up");
          }
        })
        .catch(function (response) {
          console.log(response);
          loader.hide();
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
};

/** Convert Base64 into Binary file */
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
