const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendMessageNotifications = functions.database.ref('/MESSAGES/{messageId}/messageHistory/{messageNum}').onCreate(event => {
   let messageData = event.val();
   var message = {
      notification: {
         title:`${messageData.senderName} sent you a message`,
          body: `${messageData.content}`
      },
       data: {
         title:"You have a new message from someone",
           body: `${messageData.content}`,
           isMessage: "true"
       }
   };
    return admin.messaging().sendToDevice(messageData.receiverFcmToken, message);
});