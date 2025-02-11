package com.datn.flybookapplication.Class;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<ChapterDataClass>> chapterDataList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> chapterIdList = new MutableLiveData<>();

    // Lưu danh sách ChapterDataClass
    public void setChapterDataList(List<ChapterDataClass> chapters) {
        chapterDataList.setValue(chapters);
    }

    public LiveData<List<ChapterDataClass>> getChapterDataList() {
        return chapterDataList;
    }

    // Lưu danh sách ChapterID
    public void setChapterIdList(List<String> ids) {
        chapterIdList.setValue(ids);
    }

    public LiveData<List<String>> getChapterIdList() {
        return chapterIdList;
    }
}

