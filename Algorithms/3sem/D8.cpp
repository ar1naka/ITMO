#include <iostream>
#include <string>
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
int main() {
    string s, t;
    cin >> s >> t;

    vector<int> tmp = Z(s + '#' + t + t.substr(0, t.length() - 1));

    vector<int> res;
    for (int i = 0; i < t.length() + s.length() + 1; i++) {
        if (t.length() == tmp[i]) {
            res.push_back(i - t.length() - 1);
        }
    }

    if (res.empty()) {
        res.push_back(-1);
    }
    cout << res[0];
    return 0;
}