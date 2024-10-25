//  投稿をすべて取得
const GetPosts = async (url) => {
  return await fetch(url)
    .then((res) => res.json())
    .then((json) => json.posts ?? "")
};

const SendPost = async (url, jsonData) => {
  return await fetch(url,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(jsonData)
    }
  ).then((res) =>
    res.json()
  );
};

export { GetPosts, SendPost } 