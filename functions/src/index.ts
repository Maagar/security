import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

admin.initializeApp();

export const sendSecurityNotification = functions.firestore
  .document("users/{userId}/secrets/{secretId}")
  .onCreate(async (snap, context) => {
    const userId = context.params.userId;
    const noteData = snap.data();
    const senderToken = noteData.senderToken;

    const userDoc = await admin.firestore().collection("users").doc(userId).get();
    const fcmToken = userDoc.data()?.fcmToken;

    if (!fcmToken) {
      return;
    }

    if (senderToken && senderToken === fcmToken) {
      return;
    }

    const message = {
      token: fcmToken,
      notification: {
        title: "New secret note",
        body: "A new secret note has been added. Confirm your identity.",
      },
      data: {
        navigate_to: "security_alert_screen"
      }
    };

    try {
      await admin.messaging().send(message);
    } catch (error) {
      console.error("Notification sending error", error);
    }
  });