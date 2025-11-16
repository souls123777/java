package com.example.myapplication.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.model.GitHubCommit;
import com.example.myapplication.data.model.GitHubUser;
import com.example.myapplication.data.remote.GitHubApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {
    private TextView textName, textBio, textRepos, textFollowers, textLastCommit;
    private ImageView imageAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        String username = getIntent().getStringExtra("USERNAME");

        if (!TextUtils.isEmpty(username)) {
            loadUserData(username);
        } else {
            textName.setText(R.string.error_username_missing);
        }

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        imageAvatar = findViewById(R.id.imageAvatar);
        textName = findViewById(R.id.textName);
        textBio = findViewById(R.id.textBio);
        textRepos = findViewById(R.id.textRepos);
        textFollowers = findViewById(R.id.textFollowers);
        textLastCommit = findViewById(R.id.textLastCommit);
    }

    private void loadUserData(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.github_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api = retrofit.create(GitHubApi.class);

        // 1. Загружаем профиль
        api.getUser(username).enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(@NonNull Call<GitHubUser> call, @NonNull Response<GitHubUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GitHubUser user = response.body();
                    updateUI(user);
                    loadLastCommit(username);
                } else {
                    textName.setText(R.string.error_user_not_found);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GitHubUser> call, @NonNull Throwable t) {
                textName.setText(R.string.error_network);
            }
        });
    }

    private void updateUI(GitHubUser user) {
        textName.setText(user.name != null ? user.name : user.login);
        textBio.setText(user.bio != null ? user.bio : getString(R.string.no_bio));
        textRepos.setText(getString(R.string.label_repos, user.public_repos));
        textFollowers.setText(getString(R.string.label_followers, user.followers));

        Glide.with(this)
                .load(user.avatar_url)
                .placeholder(R.drawable.ic_github)
                .into(imageAvatar);
    }

    private void loadLastCommit(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.github_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api = retrofit.create(GitHubApi.class);

        api.getEvents(username).enqueue(new Callback<GitHubCommit[]>() {
            @Override
            public void onResponse(@NonNull Call<GitHubCommit[]> call, @NonNull Response<GitHubCommit[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (GitHubCommit event : response.body()) {
                        if (getString(R.string.push_event_type).equals(event.type)
                                && event.repo != null
                                && event.created_at != null) {

                            String repoName = event.repo.name != null ? event.repo.name : getString(R.string.unknown_repo);
                            String message = getString(R.string.default_push_message);

                            if (event.payload != null
                                    && event.payload.commits != null
                                    && !event.payload.commits.isEmpty()
                                    && event.payload.commits.get(0).message != null) {
                                message = event.payload.commits.get(0).message;
                            }

                            try {
                                SimpleDateFormat input = new SimpleDateFormat(
                                        getString(R.string.github_date_format_input), Locale.getDefault());
                                SimpleDateFormat output = new SimpleDateFormat(
                                        getString(R.string.github_date_format_output), new Locale("ru"));
                                Date date = input.parse(event.created_at);
                                String formattedDate = output.format(date);

                                textLastCommit.setText(getString(R.string.last_commit_full,
                                        formattedDate, message, repoName));
                            } catch (Exception e) {
                                textLastCommit.setText(getString(R.string.last_commit_simple,
                                        message, repoName));
                            }
                            return;
                        }
                    }
                    textLastCommit.setText(R.string.no_pushes_found);
                } else {
                    textLastCommit.setText(R.string.error_response);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<GitHubCommit[]> call, @NonNull Throwable t) {
                textLastCommit.setText(getString(R.string.error_last_commit, t.getMessage()));
            }
        });
    }
}