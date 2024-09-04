#include <iostream>
#include <vector>
#include <bits/stdc++.h>
using namespace std;
string const mat = "Matching";
string const sett = "IndSet";

int main () {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int t;
    cin >> t;
    int n, m;

    while (t--) {
        cin >> n >> m;
        bool A[3 * n + 1], B[m + 1];
        memset(A, false, sizeof(A));
        memset(B, false, sizeof(B));

        int cnt = 0;
        for (int i = 1; i <= m; i++) {
            int x, y;
            cin >> x >> y;
            if (!A[x] && !A[y]) {
                A[x] = A[y] = B[i] = true;
                cnt++;
            } else {
                B[i] = false;
            }
        }
        int l = n;
        if (cnt >= n) {
            cout << mat << endl;
        } else {
            cout << sett << endl;
        }
        for (int i = 1; l; i++) {
            if (cnt >= n) {
                if (B[i]) {
                    cout << i << " ";
                    l--;
                }
            } else if (!A[i]) {
                cout << i << " ";
                l--;
            }
        }
        cout << endl;
    }
    return 0;
}