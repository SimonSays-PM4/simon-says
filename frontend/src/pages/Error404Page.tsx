import { NavLink } from "react-router-dom";

export const Error404Page: React.FC = () => {
    return (
        <div className="w-screen h-screen">
            <div className="page-content p-6 h-full my-auto">
                <div className="flex flex-col items-center justify-center">
                    <div className="text-center max-w-xl">
                        <h1 className="text-primary text-7xl drop-shadow-xl">404</h1>
                        <h4 className="text-red-500 text-lg uppercase my-7">Page Not Found</h4>
                        <p>
                            It's looking like you may have taken a wrong turn. Don't worry... it happens to the best of us. Here's a
                            little tip that might help you get back on track.
                        </p>

                        <NavLink to="/" className="items-center rounded-lg py-2 px-5 inline-flex font-semibold tracking-wide align-middle duration-500 text-sm text-center bg-primary hover:bg-primary-600 text-white mt-10">
                            <span>Go back home</span>
                        </NavLink>
                    </div>
                </div>
            </div>
        </div>
    );
}