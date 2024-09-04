import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import minimize
from sympy import Symbol, lambdify, parsing


# Main.Task №1 and №2
def gradient_descent(foo, x0, y0, eps=1e-5,
                     method='standard', callback=None):
    x, y = Symbol('x'), Symbol('y')
    parsed = parsing.parse_expr(foo)
    f = lambdify((x, y), parsed)
    f_x = lambdify((x, y), parsed.diff(x))
    f_y = lambdify((x, y), parsed.diff(y))

    methods = {
        'dichotomy': dichotomy,
        'golden_ratio': golden_ratio,
        'standard': lambda *args: 0.01
    }

    it = 0

    while True:
        it += 1

        if callback:
            callback((x0, y0, f(x0, y0)))

        l_x, l_y = f_x(x0, y0), f_y(x0, y0)

        learning_rate = (
            methods[method](f, x0, y0, l_x, l_y))
        x_k = x0 - learning_rate * l_x
        y_k = y0 - learning_rate * l_y

        if np.abs(f(x_k, y_k) - f(x0, y0)) < eps:
            break

        x0, y0 = x_k, y_k

    return {'x': (round(x_k, 5), round(y_k, 5)),
            'fun': round(f(x_k, y_k), 5), 'it': it}


# Main.Task №2
def dichotomy(f, x, y, grad_x, grad_y, eps=1e-5):
    l = eps
    r = 0.5
    delta = eps / 2

    def foo(xi):
        return x - xi * grad_x, y - xi * grad_y

    while not r - l < eps:
        x1 = (l + r - delta) / 2
        x2 = (l + r + delta) / 2
        if f(*foo(x1)) < f(*foo(x2)):
            r = x2
        else:
            l = x1

    return round((r + l) / 2, 5)


# Main.Task №3
def nelder_mead(foo, x0, y0, eps=1e-5, callback=None):
    return minimize(foo, np.array([x0, y0]), tol=eps,
                    method='Nelder-Mead', callback=callback)


# Additional.Task №1
def golden_ratio(f, x, y, grad_x, grad_y, eps=1e-5):
    l = eps
    r = 0.5
    phi = (1 + np.sqrt(5)) / 2

    def foo(xi):
        return x - xi * grad_x, y - xi * grad_y

    while not r - l < eps:
        x1 = r - (r - l) / phi
        x2 = l + (r - l) / phi
        if f(*foo(x1)) < f(*foo(x2)):
            r = x2
        else:
            l = x1

    return round((r + l) / 2, 5)


def level_lines(x, y, surface_func, bounds=(-1, 1), num=1000):
    fig = plt.figure()
    axes = fig.add_subplot()
    axes.set_xlabel('X')
    axes.set_ylabel('Y')
    axes.set_title('Level lines')

    xgrid, ygrid = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                               np.linspace(bounds[0], bounds[1], num))
    plt.scatter(x, y, s=1, color='red')
    axes.contour(xgrid, ygrid, surface_func(xgrid, ygrid))


def create_surface(res_x, res_y, res_z, surface_func, bounds=(-1, 1), num=1000):
    fig, ax = plt.subplots(subplot_kw={"projection": "3d"})
    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    ax.set_zlabel('Z')

    X, Y = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                       np.linspace(bounds[0], bounds[1], num))
    Z = surface_func(X, Y)

    ax.plot_surface(X, Y, Z, linewidth=0.1, cmap=plt.cm.coolwarm)
    ax.plot(res_x, res_y, res_z, 'r.', label='top', zorder=4, markersize=5)


def main():

    """
    :MAIN:
    Gradient descent:
    For iterations:
    f = x^2 * y^2 * ln(4 * x^2 + y^2)
    For start point:
    f = -cos(x) * cos(y) * exp(-((x - pi)^2 + (y - pi)^2))

    Nelder-Mead (scipy.optimize.minimize realization):
    For iterations:
    f = x^2 * y^2 * ln(4 * x^2 + y^2)
    For start point:
    f = -cos(x) * cos(y) * exp(-((x - pi)^2 + (y - pi)^2))

    :EXTRA:
    Functions with n variables:
    R^3: (x_1)^2 * (x_2)^2 * ln(4 * (x_1)^2 + (x_2)^2)
    R^4: 7 * (x_1)^2 + 3 * (x_2)^2 + 2 * (x_3)^2
    R^5: 15 * (x_1)^2 * ln((x_1)^2 + (x_3)^2) + (x_2)^2 + (x_4)^2
    R^6: 6 * (x_1)^2 + (x_2)^2 + 2 * (x_3)^2 + (x_4)^2 + 11 * (x_5)^2

    Poorly conditioned function:
    f = 100 * x^2 + y^2

    Noisy function:
    f = (x^2 + y^2) + random.uniform(0, 0.15)

    Multimodal function:
    f = (x^2 + y - 11)^2 + (x + y^2 - 7)^2
    """

    res_x, res_y, res_z = [], [], []

    result = nelder_mead(lambda x: x[0]**2 + x[1]**2, -1, 1)

    print(result)

    result = gradient_descent('x**2 + y**2',
                              -1, 1, method='golden_ratio')

    print(result)

    create_surface(res_x, res_y, res_z,
                   lambda x, y: x**2 + y**2, bounds=(-1, 1))

    level_lines(res_x, res_y,
                lambda x, y: x**2 + y**2, bounds=(-1, 1))

    plt.show()


if __name__ == '__main__':
    main()
