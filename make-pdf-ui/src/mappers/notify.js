import { Notify } from "quasar";

export const notifyMessage = (colorCode, response, iconName) => {
  return Notify.create({
    color: colorCode,
    message:
      (response && response) || "Woah! Danger! You are getting good at this!",
    icon: iconName,
    position: "top-right",
  });
};
