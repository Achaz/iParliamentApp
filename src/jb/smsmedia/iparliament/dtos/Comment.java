package jb.smsmedia.iparliament.dtos;

import android.graphics.Bitmap;

public class Comment {

	String sender, comment, thumbsup, thumbsdown, time, post_type, post_id, user_id;
    Bitmap profileImage, postImage;
    
    
    
	public Bitmap getPostImage() {
		return postImage;
	}

	public void setPostImage(Bitmap postImage) {
		this.postImage = postImage;
	}

	public String getPost_type() {
		return post_type;
	}

	public void setPost_type(String post_type) {
		this.post_type = post_type;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Bitmap getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(Bitmap profileImage) {
		this.profileImage = profileImage;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getThumbsup() {
		return thumbsup;
	}

	public void setThumbsup(String thumbsup) {
		this.thumbsup = thumbsup;
	}

	public String getThumbsdown() {
		return thumbsdown;
	}

	public void setThumbsdown(String thumbsdown) {
		this.thumbsdown = thumbsdown;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
