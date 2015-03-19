package jb.smsmedia.iparliament.dtos;

import android.graphics.Bitmap;

public class Feed {

	String id, post, good, bad, time, name;
	Bitmap profileImage, postImage;

	public Bitmap getPostImage() {
		return postImage;
	}

	public void setPostImage(Bitmap postImage) {
		this.postImage = postImage;
	}

	public Bitmap getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(Bitmap profileImage) {
		this.profileImage = profileImage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getGood() {
		return good;
	}

	public void setGood(String good) {
		this.good = good;
	}

	public String getBad() {
		return bad;
	}

	public void setBad(String bad) {
		this.bad = bad;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
