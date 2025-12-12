import request from "../utils/request.js";

export const getTravelInfo = (token) => {
  return request({
    url: `/travel/getTravelInfo?token=${token}`,
    method: "GET",
  });
};