#include <iostream>
#include <vector>

using namespace std;

vector<int> pref(string s){
    int n = (int) s.length();
    vector<int> p(n);
    for (int i = 1; i < n; i++){
        int j = p[i - 1];
        while (j > 0 && s[i] != s[j]){
            j = p[j - 1];
        }
        if (s[i] == s[j]){
            j++;
        }
        p[i] = j;
    }
    return p;
}


int main(){
    string s;
    cin >> s;

    string t;
    cin >> t;

    vector<int> pre = pref(t + "#" + s);
    vector<int> ans;
    for (int i = t.length(); i < t.length() + 1 + s.length(); i++) {
        if (pre[i] == t.length()) {
            ans.push_back(i - t.length() * 2);
        }
    }

    for (int i : ans){
        cout << i << " ";
    }
}