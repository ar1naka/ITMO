#include <iostream>
#include <vector>


using namespace std;
vector<int> Z(string s){
    int n = (int) s.length();
    int l = 0;
    int r = 0;
    vector<int> z(n);
    z[0] = 0;
    for (int i = 1; i < n; ++i){
        if (i <= r){
            z[i] = min(r - i + 1,z[i - l]);
        }
        while (i + z[i] < n && s[z[i]] == s[i+z[i]]){
            z[i]++;
        }
        if (i + z[i] - 1 > r){
            l = i;
            r = i + z[i] - 1;
        }
    }
    return z;
}

int main(){
    string s;
    cin >> s;
    vector<int> ans = Z(s);
    cout << ans.size() << " ";

    for (int i = 1; i < s.length();i++){
        cout << ans[i] <<  " ";
    }

}