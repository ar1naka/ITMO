#include <iostream>
#include <vector>
#include <algorithm>
#pragma GCC optimize("Ofast")
#include <iomanip>
#include <set>

#include <cmath>

using namespace std;

struct point {
    int x, y;
};

int main() {
    int n;
    cin >> n;

    vector<point> vec(n);

    for (int i = 0; i < n; i++) {
        int x, y;
        cin >> x >> y;
        //cout << x << " "<< y << endl;
        vec[i] = {x, y};
    }

    double result = 0;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            int x = vec[i].x - vec[j].x;
            int y = vec[i].y - vec[j].y;
            result = max(result, (double) x * x + y * y);
        }
    }
    cout << setprecision(15) << sqrt(result) << endl;
}