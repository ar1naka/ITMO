#include <iostream>
#include <vector>


using namespace std;
vector<int> pref(string s){
    int n = (int) s.length();
    vector<int> p(n);
    for (int i = 1; i < n; ++i){
        int j = p[i - 1];
        while (j > 0 && s[i] != s[j]){
            j = p[j - 1];
        }
        if (s[i] == s[j]){
            ++j;
        }
        p[i] = j;
    }
    return p;
}

int main(){
    string s;
    cin >> s;
    vector<int> ans = pref(s);
    cout << 0 << " ";

    for (int i = 1; i < s.length();i++){
        cout << ans[i] <<  " ";
    }

}