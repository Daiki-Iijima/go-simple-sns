import './App.css';
import { useEffect, useState } from "react"
import Post from './components/Post/Post';
import InputField from './components/InputField/InputField';

function App() {

  const URL = "http://192.168.50.143:8080/post";
  const [json, setJson] = useState(null)

  const [sendContent, setSendContent] = useState("");

  const SendPost = () => {
    fetch(URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        user_id: "11",
        content: sendContent
      })
    }).catch((error) =>
      console.log(error)
    );
  }

  const GetPosts = async (url) => {
    try {
      let response = fetch(url);
      let data = await response.then((res) => res.json());
      setJson(data);
      console.log(data);
    } catch (error) {
      setJson(null);
    }
  };

  useEffect(() => {
    GetPosts(URL);
  }, []);

  return (
    <div className="App">
      {/* ボタン */}
      <button onClick={() => {
        GetPosts(URL);
      }}>最新データ取得</button>

      {/* 投稿入力欄 */}
      <div>
        <InputField
          setSendContent={setSendContent}
          sendPost={SendPost}
        />
      </div>


      {/* 投稿一覧 */}
      {
        json && json.posts && json.posts.map((post) =>
          <Post
            key={post.id}
            content={post.content}
            userId={post.user_id}
            createTime={post.created_at}
          />
        )
      }

    </div>
  );
}

export default App;
