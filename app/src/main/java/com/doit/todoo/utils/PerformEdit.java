package com.doit.todoo.utils;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Stack;

public class PerformEdit {
    int index;
    Stack<Action> history = new Stack<>();
    Stack<Action> historyBack = new Stack<>();

    private Editable editable;
    private EditText editText;
    private boolean flag = false;

    public PerformEdit(@NonNull EditText editText) {
        this.editable = editText.getText();
        this.editText = editText;
        editText.addTextChangedListener(new Watcher());
    }

    protected void onEditableChanged(Editable s) {

    }

    protected void onTextChanged(Editable s) {

    }


    /**
     * 清理记录
     * Clear history.
     */
    public final void clearHistory() {
        history.clear();
        historyBack.clear();
    }


    /**
     * 撤销
     * Undo.
     */
    public final void undo() {
        if (history.empty()) return;
        //锁定操作
        flag = true;
        Action action = history.pop();
        historyBack.push(action);
        if (action.isAdd) {
            //撤销添加
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        } else {
            //插销删除
            editable.insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                editText.setSelection(action.startCursor, action.endCursor);
            }
        }
        flag = false;
        if (!history.empty() && history.peek().index == action.index) {
            undo();
        }
    }

    public final void redo() {
        if (historyBack.empty()) return;
        flag = true;
        Action action = historyBack.pop();
        history.push(action);
        if (action.isAdd) {
            //恢复添加
            editable.insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                editText.setSelection(action.startCursor, action.endCursor);
            }
        } else {
            //恢复删除
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        }
        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作
        if (!historyBack.empty() && historyBack.peek().index == action.index)
            redo();
    }

    /**
     * 首次设置文本
     * Set default text.
     */
    public final void setDefaultText(CharSequence text) {
        clearHistory();
        flag = true;
        editable.replace(0, editable.length(), text);
        flag = false;
    }

    private class Watcher implements TextWatcher {

        /**
         * Before text changed.
         *
         * @param s     the s
         * @param start the start 起始光标
         * @param count the endCursor 选择数量
         * @param after the after 替换增加的文字数
         */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                //删除了文字
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, false);
                    if (count > 1) {
                        //如果一次超过一个字符，说名用户选择了，然后替换或者删除操作
                        action.setSelectCount(count);
                    } else if (count == 1 && count == after) {
                        //一个字符替换
                        action.setSelectCount(count);
                    }
                    //还有一种情况:选择一个字符,然后删除(暂时没有考虑这种情况)

                    history.push(action);
                    historyBack.clear();
                    action.setIndex(++index);
                }
            }
        }

        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                //添加文字
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, true);

                    history.push(action);
                    historyBack.clear();
                    if (before > 0) {
                        //文字替换（先删除再增加），删除和增加是同一个操作，所以不需要增加序号
                        action.setIndex(index);
                    } else {
                        action.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
            if (s != editable) {
                editable = s;
                onEditableChanged(s);
            }
            PerformEdit.this.onTextChanged(s);
        }

    }

    private class Action {
        CharSequence actionTarget;
        int startCursor;
        int endCursor;
        boolean isAdd;
        int index;


        public Action(CharSequence actionTag, int startCursor, boolean add) {
            this.actionTarget = actionTag;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.isAdd = add;
        }

        public void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


    private static void CheckNull(Object o, String message) {
        if (o == null) throw new IllegalStateException(message);
    }
}