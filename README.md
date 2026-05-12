# MediaReviewApp

本や映画の「これから読みたい/観たいもの」と「読んだ/観た後の記録」を管理するWebアプリケーションです。

## 開発工程
1. 要件定義
2. 環境構築（IntelliJ/GitHub）
3. データモデル設計（Entity）
4. データ操作の実装（Repository）
5. ビジネスロジックの実装（Service）
6. 窓口の実装（Controller）
7. 画面の実装（HTML/Thymeleaf）
8. エラー処理の実装
9. テストコード作成と検証
10. Renderでの公開
11. ブラッシュアップ 

※ 新しい機能を追加するたびに「3. 設計」から「11. ブラッシュアップ」までの工程を繰り返し実施する。

## 主な機能
- **気になるリスト**: 観たい映画や読みたい本を登録。
- **レビュー**: 気になるリストのコンテンツにレビューと評価（5段階）を記入してアーカイブ。
- **クイックレビュー**: 気になるリストを経由せず、いきなりレビューを投稿することも可能。

## 技術スタック
- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Thymeleaf / Bootstrap 5

## 設定ファイル
- application.propertiesがRender（クラウド）用
- application-dev.propertiesがローカルPC用
- IntelliJで実行するときだけ、dev（自分用）の設定を使うように「実行構成の編集」をする。

## 作業予定
- 5/1週 : 環境構築
  - 5/1：IntelliJインストール、GitHubアカウント作成、要件定義
- 5/7週 : ひな形の作成
  - 5/7 要件定義、 Spring Initializr
  - 5/8 ひな形作成、Renderで公開
- 5/11週 : 説明とログイン機能の追加、テストコードの作成
  - 5/11：Gitの練習、説明の追加、エラー処理追加
  - 5/12：Java Silver演習、説明追加