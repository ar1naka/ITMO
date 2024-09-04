#include <iostream>
#include <string>
#include <vector>
#include "algorithm"
#pragma GCC optimize("Ofast")
using namespace std;

vector<int> Z(string s){
    int n = (int) s.length();
    int l = 0;
    int r = 0;
    vector<int> z(n);
    z[0] = 0;
    for (int i = 1; i < n; ++i){
        if (i <= r){
            z[i] = min(r - i + 1, z[i - l]);
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
    int n;
    string str, s;
    cin >> n;
    cin >> str;
    s = str;
    reverse(s.begin(), s.end());
    str = str + s;
    vector<int> res = Z(str);
    int start = res.size() - 1;
    int end = str.size() / 2;
    for (int i = start; i >= end ; i--){
        cout << res[i] << " ";
    }
    return 0;
}