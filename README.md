# NicoNewVideosGetter

ニコニコ動画においてお気に入りユーザーの投稿動画をRSSリーダーで一括チェックする

## Description
+ 投稿動画のRSSフィードはOPMLファイルにまとめて出力される
+ feedlyなどのRSSリーダーに生成されたOPMLファイルをインポートして使用する

## Download


## Usage
1. Account Settingにてユーザー名とパスワードを入力しログイン
2. Get FeedsでOPMLファイルが出力される
3. (feedly Web版の場合) 左カラムのOrganize feeds → 最下段のimport OPML

## Flow Chart
1. ニコニコ動画にログインする
2. お気に入りユーザーを取得する
3. ユーザーIDからそのユーザーの投稿動画を配信するRSSフィードを取得
4. OPMLファイルにRSSフィードを詰め込む
5. OPMLファイルを出力

## Author
Tokiwa
[Twitter](https://twitter.com/tkw_fms)
[HP](http://ntt_forpro.sokon.jp/)

## License
[MIT License](https://github.com/TokiwaTools/NicoNewVideosGetter/blob/master/LICENSE)
