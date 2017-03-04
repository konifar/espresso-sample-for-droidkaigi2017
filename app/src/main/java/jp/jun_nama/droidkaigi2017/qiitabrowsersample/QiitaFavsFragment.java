/*
 * Copyright (C) 2017 TOYAMA Sumio <jun.nama@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package jp.jun_nama.droidkaigi2017.qiitabrowsersample;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.jun_nama.droidkaigi2017.qiitabrowsersample.viewmodel.FavableQiitaItem;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

public class QiitaFavsFragment extends Fragment {

    private Subject<List<FavableQiitaItem>, List<FavableQiitaItem>> qiitaItemsSubject;
    private CompositeSubscription subscriptions;

    public QiitaFavsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        qiitaItemsSubject = app.getQiitaItemsSubject();
        subscriptions = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qiita_items, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        QiitaItemsAdapter adapter = new QiitaItemsAdapter();
        recyclerView.setAdapter(adapter);
        Subscription qiitaFavsUpdateSubscription = qiitaItemsSubject
                .subscribeOn(Schedulers.io())
                .flatMap(itemList -> Observable.from(itemList).filter(item -> item.isFaved).toList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setQiitaItemList);
        subscriptions.addAll(qiitaFavsUpdateSubscription);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        subscriptions.clear();
        super.onDestroyView();
    }

}
