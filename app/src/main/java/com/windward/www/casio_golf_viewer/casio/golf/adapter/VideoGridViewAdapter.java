//package com.windward.www.casio_golf_viewer.casio.golf.adapter;
//
///**
// * Created by yy on 2016/3/3.
// */
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//
//public class VideoGridViewAdapter extends BaseAdapter {
//
//    Context context;
//    private ArrayList<QA> mAllQAList = null;
//    private ImageLoader mImageLoader;
//
//    // CATEGORY_TYPE标题
//    private final int CATEGORY_TYPE = 0;
//    // QA_TYPE 问题
//    private final int QA_TYPE = 1;
//    // DIVISION_TYPE 分割部分
//    private final int DIVISION_TYPE = 2;
//    // item的种类
//    private final int TYPE_COUNT = 3;
//
//    private final String ID_CATEGORY = "ID_CATEGORY";
//    private final String ID_DIVISION = "ID_DIVISION";
//
//    public VideoGridViewAdapter(Context context) {
//        this.context = context;
//        mImageLoader = ImageLoader.getInstance();
//    }
//
//    public ArrayList<QA> getmAllQAList() {
//        return mAllQAList;
//    }
//
//    public void setAllQAList(ArrayList<QA> mAllQAList) {
//        this.mAllQAList = mAllQAList;
//    }
//
//    @Override
//    public int getCount() {
//        if (mAllQAList != null) {
//            return mAllQAList.size();
//        } else {
//            return 0;
//        }
//    }
//
//    @Override
//    public Object getItem(int arg0) {
//        if (mAllQAList != null) {
//            return mAllQAList.get(arg0);
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public long getItemId(int arg0) {
//        return arg0;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return TYPE_COUNT;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        int type = 0;
//        String id = mAllQAList.get(position).getId();
//        if (ID_CATEGORY.equals(id)) {
//            type = CATEGORY_TYPE;
//        } else if (ID_DIVISION.equals(id)) {
//            type = DIVISION_TYPE;
//        } else {
//            type = QA_TYPE;
//        }
//        // System.out.println("---------> getItemViewType() id="+id+",position="+position+",currentType="+type);
//        return type;
//
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        QAViewHolder qaViewHolder = null;
//        CategoryViewHolder categoryViewHolder = null;
//        DivisionViewHolder divisionViewHolder = null;
//
//        int currentType = getItemViewType(position);
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        if (convertView == null) {
//            switch (currentType) {
//                case CATEGORY_TYPE:
//
//                    convertView = inflater.inflate(R.layout.question_category_item,
//                            null);
//                    WWScreenUtils.initScale(convertView);
//                    categoryViewHolder = new CategoryViewHolder();
//                    categoryViewHolder.imageView = (ImageView) convertView
//                            .findViewById(R.id.question_categoryImageView);
//                    categoryViewHolder.textView = (TextView) convertView
//                            .findViewById(R.id.question_categoryTextView);
//
//                    convertView.setTag(categoryViewHolder);
//                    break;
//                case DIVISION_TYPE:
//
//                    convertView = inflater.inflate(R.layout.question_division_item,
//                            null);
//                    WWScreenUtils.initScale(convertView);
//                    divisionViewHolder = new DivisionViewHolder();
//
//                    convertView.setTag(divisionViewHolder);
//                    break;
//
//                case QA_TYPE:
//                    convertView = inflater.inflate(R.layout.question_qa_item, null);
//                    WWScreenUtils.initScale(convertView);
//                    qaViewHolder = new QAViewHolder();
//                    qaViewHolder.imageView = (CircleImageView) convertView
//                            .findViewById(R.id.question_qa_ImageView);
//                    qaViewHolder.titleTextView = (TextView) convertView
//                            .findViewById(R.id.question_qa_titleTextView);
//                    qaViewHolder.numberTextView = (TextView) convertView
//                            .findViewById(R.id.question_qa_numberTextView);
//                    qaViewHolder.detailTextView = (TextView) convertView
//                            .findViewById(R.id.question_qa_detailTextView);
//                    qaViewHolder.line = convertView.findViewById(R.id.line);
//                    qaViewHolder.answer_info = (LinearLayout) convertView
//                            .findViewById(R.id.answer_info);
//                    qaViewHolder.view1 = convertView.findViewById(R.id.view1);
//                    convertView.setTag(qaViewHolder);
//
//                    break;
//
//                default:
//                    break;
//            }
//
//        } else {
//            switch (currentType) {
//                case CATEGORY_TYPE:
//                    categoryViewHolder = (CategoryViewHolder) convertView.getTag();
//
//                    break;
//                case DIVISION_TYPE:
//                    divisionViewHolder = (DivisionViewHolder) convertView.getTag();
//
//                    break;
//                case QA_TYPE:
//                    qaViewHolder = (QAViewHolder) convertView.getTag();
//
//                    break;
//
//                default:
//                    break;
//            }
//        }
//        if (qaViewHolder != null && qaViewHolder.view1 != null) {
//            if ((position + 1) < mAllQAList.size()
//                    && getItemViewType(position + 1) == DIVISION_TYPE) {
//                qaViewHolder.view1.setVisibility(View.GONE);
//            } else {
//                qaViewHolder.view1.setVisibility(View.VISIBLE);
//            }
//        }
//        switch (currentType) {
//            case CATEGORY_TYPE:
//                if (position == 0) {
//                    categoryViewHolder.imageView
//                            .setImageResource(R.drawable.ic_recommend_xxh);
//                    categoryViewHolder.textView.setText("云介推荐");
//                } else {
//                    categoryViewHolder.imageView
//                            .setImageResource(R.drawable.ic_latest_xxh);
//                    categoryViewHolder.textView.setText("最新问题");
//                }
//                break;
//            case DIVISION_TYPE:
//
//                break;
//            case QA_TYPE:
//                qaViewHolder.titleTextView.setText(mAllQAList.get(position)
//                        .getTitle());
//                if (mAllQAList.get(position).getTop_answer().getUser() != null) {
//                    System.out.println("---首页 position="
//                            + position
//                            + ",url="
//                            + mAllQAList.get(position).getTop_answer().getUser()
//                            .getAvatar());
//                    qaViewHolder.line.setVisibility(View.VISIBLE);
//                    qaViewHolder.answer_info.setVisibility(View.VISIBLE);
//                    mImageLoader.displayImage(mAllQAList.get(position)
//                                    .getTop_answer().getUser().getAvatar(),
//                            qaViewHolder.imageView,
//                            IConstans.FENO_USER_AVATOR_OPTION);
//                    // String ddd1 = "<img[^>]+>";
//                    String str = mAllQAList.get(position).getTop_answer()
//                            .getContent();
//                    // Pattern p = Pattern.compile(ddd1);
//                    // Matcher m = p.matcher(str);
//                    // while (m.find()) {
//                    // str = str.replace(m.group(), "[图片]");
//                    // }
//                    str = str.replaceAll("\r\n", "");
//                    str = str.replaceAll("<[^>]+>", "");
//                    str = str.replaceAll("<[^>]*$", "");
//                    // str = str.replaceAll("(\\[图片\\]){2,}", "[图片]");
//                    qaViewHolder.detailTextView.setText(str);
//
//                    // if
//                    // (WWUitls.string2Int(mAllQAList.get(position).getTop_answer()
//                    // .getImage_count()) > 0) {
//                    // qaViewHolder.detailTextView.setText(mAllQAList
//                    // .get(position).getTop_answer().getContent()
//                    // + "[图片]");
//                    //
//                    // } else {
//                    // qaViewHolder.detailTextView.setText(mAllQAList
//                    // .get(position).getTop_answer().getContent());
//                    // }
//                    float count_float = WWUitls.string2Int(mAllQAList.get(position)
//                            .getTop_answer().getLike_count());
//                    qaViewHolder.numberTextView.setText(WWUitls
//                            .getCount(count_float));
//                } else {
//                    qaViewHolder.line.setVisibility(View.GONE);
//                    qaViewHolder.answer_info.setVisibility(View.GONE);
//                    // mImageLoader.displayImage(IConstans.DEFAULT_AVATAR,
//                    // qaViewHolder.imageView,
//                    // IConstans.FENO_USER_AVATOR_OPTION);
//                    // qaViewHolder.detailTextView.setText("期待你的回答");
//                    // qaViewHolder.numberTextView.setText("0");
//                }
//                qaViewHolder.imageView.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//                        // if (!WWUitls.isLogin(context)) {
//                        // context.startActivity(new Intent(context,
//                        // YunJieLoginActivity.class));
//                        // } else {
//                        if (mAllQAList.get(position).getTop_answer().getUser() != null) {
//                            // if (mAllQAList.get(position).getTop_answer()
//                            // .getUser().getId()
//                            // .equals(PreferencesUtil.getUserId(context))) {
//                            // Intent intent = new Intent(context,
//                            // YunJiePersonalInfoActivity.class);
//                            // intent.putExtra("user_id",
//                            // mAllQAList.get(position)
//                            // .getTop_answer().getUser()
//                            // .getId());
//                            // context.startActivity(intent);
//                            // } else {
//                            Intent intent = new Intent(context,
//                                    YunJieUserInfoActivity.class);
//                            intent.putExtra("user_id", mAllQAList.get(position)
//                                    .getTop_answer().getUser().getId());
//                            context.startActivity(intent);
//                            // }
//                        }
//                    }
//                    // }
//                });
//                break;
//
//            default:
//                break;
//        }
//
//        convertView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                QA qa = mAllQAList.get(position);
//                String qid = qa.getId();
//
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("questionID", qid);
//                map.put("questionTitle", qa.getTitle());
//                MobclickAgent.onEvent(context, "clickQuestion", map);
//
//                if (!ID_CATEGORY.equals(qid) && !ID_DIVISION.equals(qid)) {
//                    Intent intent = new Intent(context,
//                            YunJieQAListActivity.class);
//                    intent.putExtra("qid", qid);
//                    context.startActivity(intent);
//                }
//            }
//        });
//
//        return convertView;
//    }
//
//    class QAViewHolder {
//        CircleImageView imageView;
//        TextView titleTextView;
//        TextView detailTextView;
//        TextView numberTextView;
//        View line;
//        LinearLayout answer_info;
//        View view1;
//    }
//
//    class CategoryViewHolder {
//        ImageView imageView;
//        TextView textView;
//    }
//
//    class DivisionViewHolder {
//        TextView textView;
//    }
//
//}
//
