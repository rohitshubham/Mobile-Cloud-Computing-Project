const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
const admin = require('firebase-admin');

let serviceAccount = require('./key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

db = admin.firestore();

const payload = {
  notification: {
    title: "just published new Word",
    body: "Hii",
  }
};

// The Firebase Admin SDK to access the Firebase Realtime Database.

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.sendNotification = functions.https.onRequest(async (req, res) => {
    // Grab the text parameter.
    const email_id = req.body.email_id;
    let doc_val;
    db.collection("registrationTokens").where("email_id", "==", email_id).get().then(snapshot => {
        if (snapshot.empty) {
          res.status(404).send({"success": "false", "error" : "noEmailFound"});
        }  
    
        snapshot.forEach(doc => {
          payload.notification.title = req.body.title
          payload.notification.body = req.body.notification
          admin.messaging().sendToDevice(doc.data().registration_token, payload)
            .then((response) => {
              // Response is a message ID string.
              console.log('Successfully sent message:', response);              
            })
            .catch((error) => {
              console.log('Error sending message:', error);
            });

          res.status(200).send({"token" : doc.data().registration_token})
        });
      })
    .catch(err => {
        res.status(500).send({"success" : "false", "error": "InternalServerError"});
    });
    
  });