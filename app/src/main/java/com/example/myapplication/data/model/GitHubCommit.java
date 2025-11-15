package com.example.myapplication.data.model;

import java.util.List;

public class GitHubCommit {
    public String type;
    public String created_at;
    public Payload payload;
    public Repo repo;

    public static class Payload {
        public List<Commit> commits;
    }

    public static class Commit {
        public String message;
        public Author author;
    }

    public static class Author {
        public String name;
        public String email;
        // date НЕТ в PushEvent!
    }

    public static class Repo {
        public String name;
    }
}